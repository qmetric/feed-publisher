# feed-publisher

This library was created in order to simplify publishing data to a feed.
It handles retry strategies.

## Releasing

To release the library:

    mvn release:prepare release:perform

or just

    mvn -B release:prepare release:perform

```-B``` == ```--batch-mode``` - Mvn will assume defaults for anything that you would normally be prompted for.

This automatically changes the pom versions from 0.0.x-SNAPSHOT to 0.0.x and releases 0.0.x to nexus.
It then changes the pom versions to the next SNAPSHOT version.

## Usage

Add the following dependency to your project:

```
<dependency>
    <groupId>com.qmetric</groupId>
    <artifactId>feed-publisher</artifactId>
    <version>${VERSION}</version>
</dependency>
```

#### Basic Builder Signature
```
public static <T> FeedPublisherBuilder<T> newBuilder(final String feedUrl, final ContentMaker<T> contentMaker, final OkHttpClient okHttpClient)
```


#### Example of use

```
final Gson gson = gson();
final OkHttpClient okHttpClient = okHttpClient();

final FeedPublisherBuilder<Email> feedPublisherBuilder = FeedPublisherBuilder.newBuilder(environment.getProperty("feed.url"), email -> gson.toJson(email.params), okHttpClient);

final FeedPublisherBuilder<Email> emailFeedPublisherBuilder =
                feedPublisherBuilder.addAuthentication(environment.getProperty("feed.username"), environment.getProperty("feed.password"));

return emailFeedPublisherBuilder.createFeedPublisher();
```