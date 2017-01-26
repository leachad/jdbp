package jdbp.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SQLTable {

	boolean hasPrimaryKey() default true;

	String primaryKeyColumn() default "id";

	boolean isQueryable() default false;

	boolean isInsertable() default false;
}
