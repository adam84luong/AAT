package ch.bailu.aat_gtk.ui.view.solid

import ch.bailu.aat_gtk.ui.view.Label
import ch.bailu.aat_lib.preferences.OnPreferencesChanged
import ch.bailu.aat_lib.preferences.SolidBoolean
import ch.bailu.aat_lib.preferences.StorageInterface
import ch.bailu.gtk.GTK
import ch.bailu.gtk.gtk.Box
import ch.bailu.gtk.gtk.Orientation
import ch.bailu.gtk.gtk.Switch

class SolidBooleanSwitchView(private val solid: SolidBoolean) : OnPreferencesChanged {
    val layout = Box(Orientation.HORIZONTAL, 2)

    private val label = Label()

    private val toggle = Switch()


    init {
        label.text = solid.label
        layout.packStart(label, GTK.FALSE, GTK.FALSE, 4)
        layout.packStart(toggle, GTK.FALSE, GTK.FALSE, 4)

        toggle.active = GTK.IS(solid.value)
    }

    override fun onPreferencesChanged(storage: StorageInterface, key: String) {
        if (solid.hasKey(key)) {
            toggle.active = GTK.IS(solid.value)
        }
    }

}