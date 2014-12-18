package com.qmetric.publisher;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FeedPublisherBuilderTest
{
    @Test
    public void testName() throws Exception
    {
        final Request request = new Request.Builder()
                .url("http://example.com/")
                .build();
        final Response response = new Response.Builder()
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .code(200)
                .build();

        final OkHttpClient okHttpClient = mock(OkHttpClient.class);
        final Call call = mock(Call.class);
        when(call.execute()).thenReturn(response);
        when(okHttpClient.newCall(any(Request.class))).thenReturn(call);
        final String feedUrl = "feedPublisherBuilder";
        final ContentMaker<TData> contentMaker = o -> "content";
        final FeedPublisher<TData> feedPublisher = FeedPublisherBuilder.newBuilder(feedUrl, contentMaker, okHttpClient).createFeedPublisher();

        final Response publish = feedPublisher.publish(new TData());

        assertThat(publish.code(), equalTo(200));
    }

    static class TData
    {
    }
}