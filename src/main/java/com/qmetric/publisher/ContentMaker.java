package com.qmetric.publisher;

public interface ContentMaker<T>
{
    public String content(T t);
}
