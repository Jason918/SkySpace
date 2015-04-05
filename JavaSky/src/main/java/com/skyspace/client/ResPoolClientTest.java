package com.skyspace.client;

import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ResPoolClientTest {

    ResPoolClient client;

    @org.junit.Before
    public void setUp() throws Exception {
        client = new ResPoolClient();
    }

    @org.junit.Test
    public void testGetResValue() throws Exception {
        Integer dist = (Integer) client.getResValue("distance", -1);
        Assert.assertNotNull(dist);
        System.out.println("distance = " + dist);
    }

    @org.junit.Test
    public void testGetResValue1() throws Exception {
        List<Integer> dists = client.getResValue("distance", Integer[].class);
        Assert.assertNotNull(dists);
        System.out.println("distance = " + dists);
    }

    @org.junit.Test
    public void testSetResValue() throws Exception {
        client.setResValue("distance", 1234567);
    }

    @org.junit.Test
    public void testTicktock() throws Exception {
        client.ticktock(0.5);
        client.setResValue("distance", 1234567);
        client.ticktock(0.5);
    }

    @org.junit.Test
    public void testAddResFromFile() throws IOException {
        client.resetResPool();
        client.addResFromFile(new File("/Users/jason/lab/proj/AeroAnt/res.xml"));
    }

    @org.junit.Test
    public void testDeleteRes() throws IOException {
        client.deleteRes("season");
    }

    @org.junit.Test
    public void testTicktockToNextUpdate() throws IOException {
        client.getClock();
        client.ticktockToNextUpdate(false);
        client.getClock();
    }


}
