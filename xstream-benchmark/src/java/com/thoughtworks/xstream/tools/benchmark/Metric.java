package com.thoughtworks.xstream.tools.benchmark;

/**
 * A metric is what's actually recorded. This provides a strategy
 * for what to do with an object for a given product and should
 * return a measurable result. For example it could serialize an
 * object against a product and return how long it took to complete
 * the operation.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @see Harness
 */
public interface Metric {

    /**
     * Run the test and produce a metric.
     *
     * @param product Product to use in test.
     * @param object A object to use against the product.
     * @return The resulting metric (e.g. 12.22).
     * @throws Exception If this metric could not be obtained. This will
     *                   be reported back to the {@link Reporter}.
     * @deprecated since upcoming
     */
    double run(Product product, Object object) throws Exception;
    
    /**
     * Run the test and produce a metric.
     *
     * @param product Product to use in test.
     * @param target A target to use against the product.
     * @return The resulting metric (e.g. 12.22).
     * @throws Exception If this metric could not be obtained. This will
     *                   be reported back to the {@link Reporter}.
     * @since upcoming
     */
    double run(Product product, Target target) throws Exception;

    /**
     * The unit the metric is recorded in (for reporting purposes).
     * e.g. "ms" or "bytes".
     */
    String unit();

    /**
     * Whether a big result is better for this metric.
     */
    boolean biggerIsBetter();

}
