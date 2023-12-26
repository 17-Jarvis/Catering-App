import android.widget.TextView
import java.io.Serializable

data class storingDatabase(
    var dname: String = "",
    var dnumber: String = "",
    var dlocation: String = "",
    var dmembers: String = "",
    var dtime: String = "",
    var dservice: String = ""
) : Serializable {

    fun displayDetails(
        name: TextView,
        contact: TextView,
        location: TextView,
        members: TextView,
        time: TextView,
        service: TextView
    ) {
        name.text = dname
        contact.text = dnumber
        location.text = dlocation
        members.text = dmembers
        time.text = dtime
        service.text = dservice
    }
}
