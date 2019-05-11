package me.victor.npaw;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MainTest {



    @Before
    public void initServer() throws Exception {
        System.out.println("Starting");
        Main.main(new String[]{});
    }

    @Test
    public void testConnections() throws Exception {
        System.out.println("Connecting");
        ArrayList<Thread> threads = new ArrayList<>();

        for(int j = 0; j <=3; j++){
            System.out.println("Thread number " +  j);
            Thread t = new Thread(() -> {

                int mx = 3000;
                int i = 0;

                do{

                    i++;

                    URI uri = null;
                    try {
                        uri = new URI("http://localhost:8090/?name=" + Thread.currentThread().getName() + "-" + i);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    HttpURLConnection http = null;
                    try {
                        assert uri != null;
                        http = (HttpURLConnection) uri.toURL().openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        assert http != null;
                        http.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        assertThat("Response Code", http.getResponseCode(), is(HttpStatus.OK_200));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }while(i < mx);
            });
            threads.add(t);
            t.start();

        }

        for(Thread t : threads){
            t.join();
        }

        end();

    }


    public void end() throws Exception {
        System.out.println("ending");
        Main.end();
    }

}