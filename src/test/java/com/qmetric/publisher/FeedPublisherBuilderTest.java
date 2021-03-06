package com.qmetric.publisher;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FeedPublisherBuilderTest
{
    @Test
    public void buildsTheFeedPublisher() throws Exception
    {
        final String url = "http://example.com/";
        final Request request = new Request.Builder()
                .url(url)
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
        final ContentMaker<TData> contentMaker = o -> "content";

        final FeedPublisher<TData> feedPublisher = FeedPublisher.<TData>builder()
                .url(url)
                .contentMaker(contentMaker)
                .httpClient(okHttpClient)
                .build();

        final Response publish = feedPublisher.publish(new TData());

        assertThat(publish.code(), equalTo(200));
    }

    static class TData
    {
    }
}