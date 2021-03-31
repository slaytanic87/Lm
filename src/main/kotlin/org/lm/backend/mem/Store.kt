package org.lm.backend.mem

abstract class Store {
    var name: String;

    constructor(name: String) {
        this.name = name;
    }
}