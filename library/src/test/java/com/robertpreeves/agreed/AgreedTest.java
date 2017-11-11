package com.robertpreeves.agreed;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class AgreedTest {

    @Test
    public void basicTest() throws Exception {
        int port0 = 7234;
        int port1 = 7235;
        int port2 = 7236;

        URL url0 = new URL(String.format("http://localhost:%s", port0));
        URL url1 = new URL(String.format("http://localhost:%s", port1));
        URL url2 = new URL(String.format("http://localhost:%s", port2));

        try (
        AgreedNode<Person> node0 = AgreedNodeFactory.create((byte)0, port0, Arrays.asList(url1, url2));
        AgreedNode<Person> node1 = AgreedNodeFactory.create((byte)1, port1, Arrays.asList(url0, url2));
        AgreedNode<Person> node2 = AgreedNodeFactory.create((byte)2, port2, Arrays.asList(url0, url1))) {

        }
    }

    static class Person {
        private final String name;
        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}
