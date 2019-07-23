package reactivecircus.blueprint.interactor.rx

import io.reactivex.Observable
import io.reactivex.Scheduler
import reactivecircus.blueprint.interactor.InteractorParams

/**
 * Abstract class for a use case, representing an execution unit of asynchronous work.
 * This use case type uses [Observable] as the return type.
 * Upon subscription a use case will execute its job in the thread specified by the [ioScheduler].
 * and will post the result to the thread specified by [uiScheduler].
 */
abstract class ObservableInteractor<P : InteractorParams, T>(
    private val ioScheduler: Scheduler,
    private val uiScheduler: Scheduler
) {
    protected abstract fun createInteractor(params: P): Observable<T>

    /**
     * Build a use case observable with the provided execution thread and post execution thread
     * @param params
     * @return
     */
    fun buildObservable(params: P): Observable<T> {
        return createInteractor(params)
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
    }
}
