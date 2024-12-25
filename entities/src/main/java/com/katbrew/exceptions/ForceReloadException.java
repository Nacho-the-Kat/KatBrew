package com.katbrew.exceptions;

/**
 * Custom Exceptiom um einen Relaod in der Ui zu forcen.
 * Die Exception forced den Reaload selbst nicht. Allerdings, kann die Exception
 * einzeln abgefangen werden und somit das Reaload Signal einfacher an die Ui versendet werden.
 */
public class ForceReloadException extends Exception {

}
