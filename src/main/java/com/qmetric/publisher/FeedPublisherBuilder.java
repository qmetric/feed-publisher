package com.qmetric.publisher;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FeedPublisherBuilder<T>
{
    private final OkHttpClient client;
    private final String feedUrl;
    private final ContentMaker<T> contentMaker;
    private final Retryer<Response> retryer;

    private FeedPublisherBuilder(final String feedUrl, final ContentMaker<T> contentMaker, final OkHttpClient okHttpClient)
    {
        this.feedUrl = feedUrl;
        this.contentMaker = contentMaker;
        this.retryer = FeedPublisherBuilder.RETRYER_BUILDER.build();
        this.client = okHttpClient;
    }

    public static <T> FeedPublisherBuilder<T> newBuilder(final String feedUrl, final ContentMaker<T> contentMaker, final OkHttpClient okHttpClient)
    {
        return new FeedPublisherBuilder<>(feedUrl, contentMaker, okHttpClient);
    }

    private static RetryerBuilder<Response> RETRYER_BUILDER = RetryerBuilder.<Response>newBuilder() //
            .retryIfExceptionOfType(IOException.class) //
            .withWaitStrategy(WaitStrategies.fixedWait(2, SECONDS)) //
            .withStopStrategy(StopStrategies.stopAfterAttempt(60)); //


    public FeedPublisher<T> createFeedPublisher()
    {
        if (contentMaker == null)
        {
            throw new IllegalStateException("content maker not assigned");
        }

        return new FeedPublisher<>(client, feedUrl, contentMaker, retryer);
    }
}