package com.qmetric.publisher;

public interface ContentMaker<T>
{
    String content(T t);
}
