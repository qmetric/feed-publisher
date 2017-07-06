# feed-publisher

This library was created in order to simplify publishing data to a feed.
It handles retry strategies.

Basic Builder Signature
```
public static <T> FeedPublisherBuilder<T> newBuilder(final String feedUrl, final ContentMaker<T> contentMaker, final OkHttpClient okHttpClient)
```

Example of use

```
final Gson gson = gson();
final OkHttpClient okHttpClient = okHttpClient();

final FeedPublisherBuilder<Email> feedPublisherBuilder = FeedPublisherBuilder.newBuilder(environment.getProperty("feed.url"), email -> gson.toJson(email.params), okHttpClient);

final FeedPublisherBuilder<Email> emailFeedPublisherBuilder =
                feedPublisherBuilder.addAuthentication(environment.getProperty("feed.username"), environment.getProperty("feed.password"));

return emailFeedPublisherBuilder.createFeedPublisher();
```
