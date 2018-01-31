package de.tfr.app.haproxy

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.Push
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dependency.StyleSheet
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.renderer.TemplateRenderer
import com.vaadin.flow.router.Route
import com.vaadin.flow.shared.communication.PushMode
import com.vaadin.flow.shared.ui.Transport
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import de.tfr.app.haproxy.board.parser.HAService
import de.tfr.app.haproxy.template.ExampleTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import kotlin.concurrent.thread


/**
 * The main view contains a simple label element and a template element.
 */
@BodySize(height = "100vh", width = "100vw")
@StyleSheet("context://styles.css")
@Route("")
@Theme(Lumo::class)
@Push(transport = Transport.LONG_POLLING)
//@SpringUI()
class MainView(@Autowired template: ExampleTemplate, @Autowired val haproxy: HAProxyService,
               @Autowired @Value("\${haproxy.service.update-interval}") updateInterval: String
) : Composite<Div>() {
    private val table: Grid<HAService>
    private val updateInfo: Label
    private val updateInterval: Long = updateInterval.toLong()
    private var updateCounter = 0L
    private val filter: Checkbox

    init {
        filter = Checkbox("Only Fails")
        updateInfo = Label("Update")

        content.add(updateInfo)
        content.add(filter)
        content.add(template)
        table = Grid()
        table.setItems(getServices())
        table.addColumn(TemplateRenderer.of<HAService>(
                "<span theme$='[[item.badgeStyle]]'>[[item.status]]</span>")
                .withProperty("status", HAService::status)
                .withProperty("style", { it.status.style() })
                .withProperty("badgeStyle", { it.status.badgeStyle() })
        )
        table.addColumn(HAService::name)
        table.addColumn(HAService::node)

        content.add(table)

        filter.addClickListener { updateHAServiceStats() }

        updateHAServiceStats()
        enablePush()
        startUpdateService()
    }

    private fun updateHAServiceStats() {
        val update = getServices()
        updateCounter++
        ui.ifPresent {
            it.access {
                updateInfo.text = ("Update " + updateCounter)
                table.setItems(update)
                table.dataProvider.refreshAll()
            }
        }
    }

    private fun startUpdateService() {
        thread {
            while (true) {
                updateHAServiceStats()
                Thread.sleep(updateInterval * 1000)
            }
        }
    }

    private fun getServices(): List<HAService> {
        val stats = haproxy.getStats().filter { !it.isSummary() }
        return if (filter.value) {
            stats.filter { it.isDown() }
        } else {
            stats
        }
    }

    private fun HAService.Status.badgeStyle() = "badge ${this.style()} primary"

    private fun HAService.Status.style() = when (this) {
        HAService.Status.Up -> "success"
        HAService.Status.NoCheck, HAService.Status.Unknown -> "contrast"
        else -> "error"
    }

    private fun HAService.isSummary() = when (this.node) {
        "BACKEND", "FRONTEND" -> true
        else -> false
    }

    private fun enablePush() {
        UI.getCurrent().pushConfiguration.pushMode = PushMode.AUTOMATIC
    }

}