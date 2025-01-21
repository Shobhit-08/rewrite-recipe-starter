package com.yourorg;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

import static org.openrewrite.java.Assertions.java;

public class ReplaceMapOfWithUnmodifiableFixedOrderMapTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new ReplaceMapOfWithUnmodifiableFixedOrderMap())
          .typeValidationOptions(TypeValidation.builder().classDeclarations(false).build());
    }

    @Test
    void replaceMapOfForEmptyMap() {
        rewriteRun(
          //language=java
          java(
            """
            import java.util.Map;
            import static com.example.Constants.*;

            public class TestClass {            
    
                void test() {
                    Map<String, Boolean> map = Map.of();
                }
            }
            """,
            """
            import java.util.Map;
            import static com.example.Constants.*;
            import com.yourorg.UnmodifiableFixedOrderMap;

            public class TestClass {
                
                void test() {
                    Map<String, Boolean> map = UnmodifiableFixedOrderMap.<String, Boolean>builder()
                        .build();
                }
            }
            """
          )
        );
    }

    @Test
    void replaceMapOfForEnums() {
        rewriteRun(
          //language=java
          java(
            """
            import com.google.protobuf.JavaType;import java.util.Map;
            import static com.example.Constants.*;

            public class TestClass {  
    
                void test() {
                    Map<String, Boolean> map = Map.of(JavaType.BOOLEAN, false, JavaType.DOUBLE, true);
                }
            }
            """,
            """
            import com.google.protobuf.JavaType;
            import java.util.Map;
            import static com.example.Constants.*;
            import com.yourorg.UnmodifiableFixedOrderMap;

            public class TestClass { 
    
                void test() {
                    Map<String, Boolean> map = UnmodifiableFixedOrderMap.<JavaType, Boolean>builder()
                    .put(JavaType.BOOLEAN, false)
                    .put(JavaType.DOUBLE, true)
                        .build();
                }
            }
            """
          )
        );
    }

    @Test
    void replaceMapOf() {
        rewriteRun(
          //language=java
          java(
            """
            import java.util.Map;
            import static com.example.Constants.*;

            public class TestClass {
                void test() {
                    Map<String, Boolean> map = Map.of("POLARIS_ROUTER_KEY", false, "PERCENTAGE_BASED_ROUTER_KEY", true);
                }
            }
            """,
            """
            import java.util.Map;
            import static com.example.Constants.*;
            import com.yourorg.UnmodifiableFixedOrderMap;

            public class TestClass {
                void test() {
                    Map<String, Boolean> map = UnmodifiableFixedOrderMap.<String, Boolean>builder()
                        .put("POLARIS_ROUTER_KEY", false)
                        .put("PERCENTAGE_BASED_ROUTER_KEY", true)
                        .build();
                }
            }
            """
          )
        );
    }
}
