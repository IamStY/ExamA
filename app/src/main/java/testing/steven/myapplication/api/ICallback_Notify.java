package testing.steven.myapplication.api;

public interface ICallback_Notify<T> {
    void dataFetched(T data);
    void failure();

}