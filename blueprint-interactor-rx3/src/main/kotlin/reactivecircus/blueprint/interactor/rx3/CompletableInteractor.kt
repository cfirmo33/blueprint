package reactivecircus.blueprint.interactor.rx3

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import reactivecircus.blueprint.interactor.InteractorParams

/**
 * Abstract class for a use case, representing an execution unit of asynchronous work.
 * This use case type uses [Completable] as the return type.
 * Upon subscription a use case will execute its job in the thread specified by the [ioScheduler].
 * and will post the result to the thread specified by [uiScheduler].
 */
public abstract class CompletableInteractor<P : InteractorParams>(
    private val ioScheduler: Scheduler,
    private val uiScheduler: Scheduler
) {

    /**
     * Create a [Completable] for this interactor.
     */
    protected abstract fun createInteractor(params: P): Completable

    /**
     * Build a use case with the provided execution thread and post execution thread
     */
    public fun buildCompletable(params: P): Completable {
        return createInteractor(params)
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
    }
}
