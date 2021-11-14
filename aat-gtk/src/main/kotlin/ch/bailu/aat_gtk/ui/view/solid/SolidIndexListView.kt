package ch.bailu.aat_gtk.ui.view.solid

import ch.bailu.aat_lib.preferences.SolidIndexList

class SolidIndexListView(private val solid: SolidIndexList) : SolidLabelTextView(solid) {

    override fun onRequestNewValue() {
        solid.cycle()
    }

}