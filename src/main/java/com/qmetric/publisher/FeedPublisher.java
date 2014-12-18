package com.qmetric.publisher;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class FeedPublisher<T>
{
    private final static Logger LOGGER = LoggerFactory.getLogger(FeedPublisher.class);

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final ContentMaker<T> contentMaker;

    private Retryer<Response> retryer;

    private final String feedUrl;

    private final OkHttpClient client;

    public FeedPublisher(final OkHttpClient client, final String feedUrl, final ContentMaker<T> contentMaker, final Retryer<Response> retryer)
    {
        this.client = client;
        this.feedUrl = feedUrl;
        this.contentMaker = contentMaker;
        this.retryer = retryer;
    }

    public Response publish(final T t) throws ExecutionException, RetryException
    {
        return retryer.call(() -> publishToFeed(t));
    }

    private Response publishToFeed(final T t) throws IOException
    {
        final String content = contentMaker.content(t);

        RequestBody body = RequestBody.create(JSON, content);
        Request request = new Request.Builder().url(feedUrl).post(body).build();
        Response response = client.newCall(request).execute();
        LOGGER.info(String.format("Published %s %s to %s", t.getClass().getName(), t, feedUrl));
        return response;
    }
}