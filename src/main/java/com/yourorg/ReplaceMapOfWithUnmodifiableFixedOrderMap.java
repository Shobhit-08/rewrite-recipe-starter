package com.yourorg;

import org.openrewrite.ExecutionContext;
import org.openrewrite.NlsRewrite;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.search.UsesMethod;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;

import java.util.ArrayList;
import java.util.List;

public class ReplaceMapOfWithUnmodifiableFixedOrderMap extends Recipe {

    private static final MethodMatcher MATCHER = new MethodMatcher("java.util.Map of(..)");

    @Override
    public @NlsRewrite.DisplayName String getDisplayName() {
        return "Use `UnMofifiableFixedOrderMap` instead of `Map.of()` or `Map.ofEntries()`";
    }

    @Override
    public @NlsRewrite.Description String getDescription() {
        return "UnMofifiableFixedOrderMap gives fixed order of iteration on successive test runs which prevents" +
                " randomness during regression using Snapshot-testing.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(
                Preconditions.or(new UsesMethod<>(MATCHER)),
                new JavaVisitor<ExecutionContext>() {

                    @Override
                    public J visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
                        J.MethodInvocation m = (J.MethodInvocation) super.visitMethodInvocation(method, ctx);
                        if (MATCHER.matches(method)) {
                            maybeRemoveImport("java.util.Map");
                            maybeAddImport("com.phonepe.payments.paymentservice.util.UnmodifiableFixedOrderMap");

                            List<Expression> argumentsToPut = new ArrayList<>();

                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("UnmodifiableFixedOrderMap.<String, Boolean>builder()\n");
                            List<Expression> args = m.getArguments();

                            for (int i = 0; i < args.size(); i += 2) {
                                argumentsToPut.add(args.get(i));
                                argumentsToPut.add(args.get(i + 1));
                                stringBuilder.append(".put(");
                                stringBuilder.append("#{any()}");
                                stringBuilder.append(",");
                                stringBuilder.append("#{any()}");
                                stringBuilder.append(")\n");
                            }
                            stringBuilder.append(".build()");
                            return JavaTemplate.builder(stringBuilder.toString())
                                    .contextSensitive()
                                    .imports("com.phonepe.payments.paymentservice.util.UnmodifiableFixedOrderMap")
                                    .build()
                                    .apply(updateCursor(m),
                                            m.getCoordinates().replace(),
                                            argumentsToPut.toArray());
                        }
                        return m;

                    }
                });
    }
}
