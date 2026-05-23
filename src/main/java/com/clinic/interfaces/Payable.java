// File: com/clinic/interfaces/Payable.java
package com.clinic.interfaces;

/**
 * Interface representing entities that can generate a billing amount.
 * Implemented by services or appointments that carry a cost.
 */
public interface Payable {

    /**
     * Calculates the total amount due for this entity.
     *
     * @return the billing amount as a double
     */
    double calculateCost();

    /**
     * Returns a human-readable billing description.
     *
     * @return billing summary string
     */
    String getBillingDescription();
}