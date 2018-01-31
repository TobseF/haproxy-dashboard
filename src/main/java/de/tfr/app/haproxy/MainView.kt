package de.tfr.app.haproxy

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dependency.StyleSheet
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.renderer.TemplateRenderer
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import de.tfr.app.haproxy.board.parser.HAService
import de.tfr.app.haproxy.template.ExampleTemplate
import org.springframework.beans.factory.annotation.Autowired


/**
 * The main view contains a simple label element and a template element.
 */
@BodySize(height = "100vh", width = "100vw")
@StyleSheet("context://styles.css")
@Route("")
@Theme(Lumo::class)
class MainView(@Autowired template: ExampleTemplate, @Autowired val haproxy: HAProxyService) : Composite<Div>() {

    init {
        val filter = Checkbox("Only Fails")
        content.add(filter)
        content.add(template)
        val table = Grid<HAService>()

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

        filter.addClickListener {
            if (it.source.value) {
                table.setItems(getServices().filter { !it.isUp() })
            } else {
                table.setItems(getServices())
            }
        }

    }

    private fun getServices() = haproxy.getStats().filter { !it.isSummary() }

    private fun HAService.Status.badgeStyle() = "badge ${this.style()} primary"

    private fun HAService.Status.style() = when (this) {
        HAService.Status.Up -> "success"
        HAService.Status.NoCheck -> "contrast"
        else -> "error"
    }

    private fun HAService.isSummary() = when (this.node) {
        "BACKEND", "FRONTEND" -> true
        else -> false
    }

}