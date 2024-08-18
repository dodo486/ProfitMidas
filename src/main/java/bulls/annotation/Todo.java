package bulls.annotation;

public @interface Todo {

    enum Importance {CRITICAL, SERIOUS, TRIVIAL, CHECK}

    Importance importance() default Importance.CHECK;

    String msg();

}
