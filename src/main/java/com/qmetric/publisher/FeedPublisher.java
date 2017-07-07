package com.qmetric.publisher;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Preconditions;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FeedPublisher<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedPublisher.class);

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

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

    public Response publish(final T t) throws RetryException
    {
        try
        {
            return retryer.call(() -> publishToFeed(t));
        }
        catch (ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Response publishToFeed(final T t) throws IOException
    {
        final String content = contentMaker.content(t);

        RequestBody body = RequestBody.create(JSON, content);
        Request request = new Request.Builder().url(feedUrl).post(body).build();
        Response response = client.newCall(request).execute();

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info(String.format("Published %s to %s", body, feedUrl));
        }

        return response;
    }

    public static class Builder<T>
    {
        private ContentMaker<T> contentMaker;

        private OkHttpClient httpClient;

        private String url;

        private Retryer<Response> retryer = RetryerBuilder.<Response>newBuilder() //
                .retryIfExceptionOfType(IOException.class) //
                .withWaitStrategy(WaitStrategies.fixedWait(2, SECONDS)) //
                .withStopStrategy(StopStrategies.stopAfterAttempt(60)).build(); //

        public Builder<T> url(String url)
        {
            this.url = url;
            return this;
        }

        public Builder<T> httpClient(OkHttpClient httpClient)
        {
            this.httpClient = httpClient;
            return this;
        }

        public Builder<T> contentMaker(ContentMaker<T> contentMaker)
        {
            this.contentMaker = contentMaker;
            return this;
        }

        public Builder<T> retyer(Retryer<Response> retryer)
        {
            this.retryer = retryer;
            return this;
        }

        public Builder<T> notRetrying()
        {
            this.retryer = RetryerBuilder.<Response>newBuilder().build();
            return this;
        }

        public FeedPublisher<T> build()
        {
            Preconditions.checkNotNull(contentMaker);
            Preconditions.checkNotNull(httpClient);
            Preconditions.checkNotNull(url);
            Preconditions.checkNotNull(retryer);

            return new FeedPublisher<>(httpClient, url, contentMaker, retryer);
        }
    }

    public static <T> Builder<T> builder()
    {
        return new Builder<>();
    }
}
