package org.lm.backend.codegenerator

import org.lm.backend.mem.Register
import org.lm.backend.mem.Store
import org.lm.frontend.syntaxtree.Id
import java.util.*

class RegisterAlloc {

    var addrDesriptorRepoMap: Hashtable<Int, Deque<Store>> = Hashtable();
    var addrSetDescriptorMap: Hashtable<Int, Hashtable<Id, Store>> = Hashtable();

    init {
        addrDesriptorRepoMap.put(8, register8());
        addrDesriptorRepoMap.put(16, register16());
        addrDesriptorRepoMap.put(32, register32());
        addrDesriptorRepoMap.put(64, register64());

        addrSetDescriptorMap.put(8, Hashtable<Id, Store>());
        addrSetDescriptorMap.put(16, Hashtable<Id, Store>());
        addrSetDescriptorMap.put(32, Hashtable<Id, Store>());
        addrSetDescriptorMap.put(64, Hashtable<Id, Store>());
    }

    private fun register16(): Deque<Store> {
        val registerDescriptor: Deque<Store> = LinkedList<Store>();
        registerDescriptor.add(Register("r8w", 16));
        registerDescriptor.add(Register("r9w", 16));
        registerDescriptor.add(Register("r10w", 16));
        registerDescriptor.add(Register("r11w", 16));
        registerDescriptor.add(Register("r12w", 16));
        registerDescriptor.add(Register("r13w", 16));
        registerDescriptor.add(Register("r14w", 16));
        registerDescriptor.add(Register("r15w", 16));
        return registerDescriptor;
    }

    private fun register32(): Deque<Store> {
        val registerDescriptor: Deque<Store> = LinkedList();
        registerDescriptor.add(Register("r8d", 32));
        registerDescriptor.add(Register("r9d", 32));
        registerDescriptor.add(Register("r10d", 32));
        registerDescriptor.add(Register("r11d", 32));
        registerDescriptor.add(Register("r12d", 32));
        registerDescriptor.add(Register("r13d", 32));
        registerDescriptor.add(Register("r14d", 32));
        registerDescriptor.add(Register("r15d", 32));
        return registerDescriptor;
    }

    private fun register8():Deque<Store> {
        val registerDescriptor: Deque<Store> = LinkedList();
        registerDescriptor.add(Register("sp1", 8));
        registerDescriptor.add(Register("bpl", 8));
        registerDescriptor.add(Register("sil", 8));
        registerDescriptor.add(Register("dil", 8));
        registerDescriptor.add(Register("r8b", 8));
        registerDescriptor.add(Register("r9b", 8));
        registerDescriptor.add(Register("r10b", 8));
        registerDescriptor.add(Register("r11b", 8));
        registerDescriptor.add(Register("r12b", 8));
        registerDescriptor.add(Register("r13b", 8));
        registerDescriptor.add(Register("r14b", 8));
        registerDescriptor.add(Register("r15b", 8));
        return registerDescriptor;
    }

    private fun register64():Deque<Store> {
        val registerDescriptor: Deque<Store> = LinkedList();
        registerDescriptor.add(Register("rax", 64));
        registerDescriptor.add(Register("rcx", 64));
        registerDescriptor.add(Register("rdx", 64));
        registerDescriptor.add(Register("rsp", 64));
        registerDescriptor.add(Register("rbp", 64));
        registerDescriptor.add(Register("rsi", 64));
        registerDescriptor.add(Register("rdi", 64));
        registerDescriptor.add(Register("r8", 64));
        registerDescriptor.add(Register("r9", 64));
        registerDescriptor.add(Register("r10", 64));
        registerDescriptor.add(Register("r11", 64));
        registerDescriptor.add(Register("r12", 64));
        registerDescriptor.add(Register("r13", 64));
        registerDescriptor.add(Register("r14", 64));
        registerDescriptor.add(Register("r15", 64));
        return registerDescriptor;
    }

    public fun getRegisterFromLocation(id: Id): Store? {
        val bitLength = id.type!!.width * 8;
        val registerSet: Hashtable<Id, Store> = addrSetDescriptorMap.get(bitLength)!!;
        return registerSet.get(id);
    }

    public fun getNextFreeRegister(id: Id): Store {
        val bitLength = id.type!!.width * 8;
        val registerList: Deque<Store> = addrDesriptorRepoMap.get(bitLength)
            ?: throw Error("No register was found for bit length: " + bitLength);
        if (registerList.isEmpty()) {
            throw Error("There is no any free register for variable: " + id);
        }
        val freeRegister: Store = registerList.removeFirst();
        val registerSet: Hashtable<Id, Store> = addrSetDescriptorMap.get(bitLength)!!;
        registerSet.put(id, freeRegister);
        return freeRegister;
    }

}