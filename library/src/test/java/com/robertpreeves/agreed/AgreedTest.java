package com.robertpreeves.agreed;

import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;

import java.util.Arrays;
import java.util.Objects;

public class AgreedTest {

    @Test
    public void basicTest() throws Exception {
        int port0 = 7234;
        int port1 = 7235;
        int port2 = 7236;

        String hostname = "localhost";
        InetSocketAddress socket0 = new InetSocketAddress(hostname, port0);
        InetSocketAddress socket1 = new InetSocketAddress(hostname, port1);
        InetSocketAddress socket2 = new InetSocketAddress(hostname, port2);

        try (
        AgreedNode<Person> node0 = AgreedNodeFactory.create((byte)0, port0, Arrays.asList(socket1, socket2));
        AgreedNode<Person> node1 = AgreedNodeFactory.create((byte)1, port1, Arrays.asList(socket0, socket2));
        AgreedNode<Person> node2 = AgreedNodeFactory.create((byte)2, port2, Arrays.asList(socket0, socket1))) {

            // Propose a value
            Person john = new Person("john", 40);
            Person accepted = node0.propose(john);

            // Verify proposed value
            Assert.assertEquals(john, accepted);
            for (AgreedNode<Person> node :
                    Arrays.asList(node0, node1, node2)) {
                Assert.assertEquals(john, node.current());
            }
        }
    }

    static class Person {
        private final String name;
        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + name.hashCode();
            result = 31 * result + age;

            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Person)) {
                return false;
            }

            Person other = (Person)obj;

            return Objects.equals(name, other.getName()) && Objects.equals(age, other.getAge());
        }
    }
}
