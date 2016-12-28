package com.sam_chordas.android.stockhawk.loader;

import android.content.Context;
import android.support.v4.content.Loader;

import com.google.android.agera.Result;
import com.google.android.agera.Updatable;

import org.jetbrains.annotations.NotNull;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Metadata(
        mv = {1, 1, 1},
        bv = {1, 0, 0},
        k = 1,
        d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\b&\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u00022\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00040\u00032\b\u0012\u0004\u0012\u0002H\u00010\u00052\u00020\u0006B\r\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\b\u0010\u0012\u001a\u00020\u0013H\u0014J\u001e\u0010\u0014\u001a\u00020\u00152\f\u0010\n\u001a\b\u0012\u0004\u0012\u00028\u00000\u000b2\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J\b\u0010\u0018\u001a\u00020\u0015H\u0014J\b\u0010\u0019\u001a\u00020\u0015H\u0014J$\u0010\u001a\u001a\u00020\u00152\f\u0010\n\u001a\b\u0012\u0004\u0012\u00028\u00000\u000b2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00028\u00000\u001cH\u0016J\b\u0010\u001d\u001a\u00020\u0015H\u0014J\u0015\u0010\u001e\u001a\u00028\u00012\u0006\u0010\u001f\u001a\u00028\u0000H&¢\u0006\u0002\u0010 J\b\u0010!\u001a\u00020\u0015H\u0016R\u0018\u0010\n\u001a\b\u0012\u0004\u0012\u00028\u00000\u000bX¦\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0016\u0010\u000e\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R!\u0010\u000f\u001a\u0015\u0012\f\u0012\n \u0010*\u0004\u0018\u00018\u00018\u00010\u0004¢\u0006\u0002\b\u0011X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\""},
        d2 = {"Lcom/sam_chordas/android/stockhawk/loader/RetrofitLoader;", "Body", "D", "Landroid/support/v4/content/Loader;", "Lcom/google/android/agera/Result;", "Lretrofit2/Callback;", "Lcom/google/android/agera/Updatable;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "call", "Lretrofit2/Call;", "getCall", "()Lretrofit2/Call;", "lastCall", "lastResult", "kotlin.jvm.PlatformType", "Landroid/support/annotation/NonNull;", "onCancelLoad", "", "onFailure", "", "t", "", "onForceLoad", "onReset", "onResponse", "response", "Lretrofit2/Response;", "onStartLoading", "transformResponse", "body", "(Ljava/lang/Object;)Ljava/lang/Object;", "update", "production sources for module app"}
)
public abstract class RetrofitLoader<Body, D> extends Loader<Result<D>> implements Callback<Body>, Updatable {
    private Result<D> lastResult;
    private Call<Body> lastCall;

    @NotNull
    public abstract Call<Body> getCall();

    public abstract D transformResponse(Body var1);

    public void update() {
        Call<Body> var10000 = this.lastCall;
        if (this.lastCall != null) {
            var10000.cancel();
        }

        this.lastCall = this.getCall();
        var10000 = this.lastCall;
        if (this.lastCall == null) {
            Intrinsics.throwNpe();
        }

        var10000.enqueue(this);
    }

    protected void onStartLoading() {
        this.deliverResult(this.lastResult);
        if (!this.lastResult.succeeded()) {
            this.update();
        }

    }

    protected void onForceLoad() {
        this.deliverResult(this.lastResult);
        this.update();
    }

    protected void onReset() {
        Call var10000 = this.lastCall;
        if (this.lastCall != null) {
            var10000.cancel();
        }

        this.lastResult = Result.absent();
    }

    protected boolean onCancelLoad() {
        Call<Body> var10000 = this.lastCall;
        if (this.lastCall != null) {
            Call<Body> var1 = var10000;
            Call<Body> $receiver = var1;
            $receiver.cancel();
            return true;
        } else {
            return false;
        }
    }

    public void onResponse(@NotNull Call<Body> call, @NotNull Response<Body> response) {
        Intrinsics.checkParameterIsNotNull(call, "call");
        Intrinsics.checkParameterIsNotNull(response, "response");
        this.lastResult = response.isSuccessful() ? Result.success(this.transformResponse(response.body())) : (Result<D>) Result.failure();
        this.deliverResult(this.lastResult);
    }

    public void onFailure(@NotNull Call call, @NotNull Throwable t) {
        Intrinsics.checkParameterIsNotNull(call, "call");
        Intrinsics.checkParameterIsNotNull(t, "t");
        this.lastResult = Result.failure(t);
        this.deliverResult(this.lastResult);
    }

    public RetrofitLoader(@NotNull Context context) {
        super(context);
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.lastResult = Result.absent();
    }
}
