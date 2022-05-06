package tutil;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OpCodes {
    /**opcode for handler in thandler/ */
    int value();

    /**handler register set */
    boolean disabled() default false;
}
