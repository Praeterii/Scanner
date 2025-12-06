# --- Performance Optimizations ---

# Allow R8/ProGuard to change the visibility of classes and class members.
# This enables more aggressive inlining and code optimization.
-allowaccessmodification

# Flatten the package hierarchy to reduce the size of the DEX file and improve load times.
# Moves all classes to the default package (root).
-repackageclasses ''

# Remove logging calls in release builds to reduce overhead and code size.
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove System.out logging calls
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}

# Remove Kotlin null checks (Intrinsics).
# WARNING: This improves performance and reduces size, but removes runtime null safety checks.
# Only uncomment if you are confident in your code's null safety and have extensive testing.
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkNotNullParameter(...);
    public static void checkExpressionValueIsNotNull(...);
    public static void checkNotNull(...);
    public static void checkParameterIsNotNull(...);
    public static void checkReturnedValueIsNotNull(...);
    public static void throwNpe(...);
    public static void throwJavaNpe(...);
    public static void throwUninitializedPropertyAccessException(...);
}