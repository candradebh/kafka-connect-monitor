package com.kafka.connect.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Define que a anotação estará disponível em tempo de execução
@Target(ElementType.TYPE) // Define que a anotação pode ser aplicada a classes e interfaces
public @interface Auditable
{
}
