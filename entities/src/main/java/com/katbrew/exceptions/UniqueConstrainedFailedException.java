package com.katbrew.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jooq.TableField;

/**
 * Exception, falls Unique Constraints in der Datenbank failen.
 */
@AllArgsConstructor
public class UniqueConstrainedFailedException extends RuntimeException {

    /**
     * Das Unique Feld, welches die Exception geworfen hat.
     */
    @Getter
    final TableField tableField;

    /**
     * Liefert die Fehlermeldung.
     *
     * @return Die Fehlermeldung.
     */
    @Override
    public String getMessage() {
        return "Unique constraint failed for column: " + tableField.getName();
    }
}
