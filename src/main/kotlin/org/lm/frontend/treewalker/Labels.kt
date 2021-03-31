package org.lm.frontend.treewalker

/**
 * This class describes a pair of (trueLabel, falseLabel) and
 * the label nextLabel, which needs for translation of controlling structure in
 * the given programming language.
 */
class Labels {

    var first: Int;
    var second: Int;

    constructor(first: Int, second: Int) {
        this.first = first;
        this.second= second;
    }

    /**
     * As null value
     */
    constructor(first: Int) {
        this.first = first;
        this.second = -1;
    }

    public fun trueLabel(): Int {
        return first;
    }

    public fun falseLabel(): Int {
        return second;
    }

    public fun nextLabel(): Int {
        return first;
    }

}