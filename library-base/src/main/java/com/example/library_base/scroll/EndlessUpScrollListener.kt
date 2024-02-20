package ai.ftech.flive.presentation.widget.recyclerview.scroll

import androidx.recyclerview.widget.RecyclerView
import com.example.library_base.scroll.BaseRecyclerViewEndlessScrollListener

abstract class EndlessUpScrollListener(
    private var layoutManager: RecyclerView.LayoutManager
) : BaseRecyclerViewEndlessScrollListener(layoutManager) {

    private var isScrolledUp = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        isScrolledUp = dy < 0
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_SETTLING && isScrolledUp) {
            val totalItemCount = layoutManager.itemCount

            if (!isLoading && totalItemCount > 0) {
                if (!lastPage) {
                    onLoadMore()
                    isLoading = true
                }
            }
        }
    }
}
