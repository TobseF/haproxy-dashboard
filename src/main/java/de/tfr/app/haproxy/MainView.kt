package de.tfr.app.haproxy

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dependency.StyleSheet
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.router.Route
import de.tfr.app.haproxy.board.parser.HAService
import de.tfr.app.haproxy.template.ExampleTemplate
import org.springframework.beans.factory.annotation.Autowired

/**
 * The main view contains a simple label element and a template element.
 */
@BodySize(height = "100vh", width = "100vw")
@StyleSheet("context://styles.css")
@Route("")
class MainView(@Autowired template: ExampleTemplate, @Autowired val haproxy: HAProxyService) : Composite<Div>() {

    init {
        val filter = Checkbox("Only Fails")
        content.add(filter)
        content.add(template)
        val table = Grid<HAService>()

        val services = haproxy.getStats()

        table.setItems(services)
        table.addColumn(HAService::name)
        table.addColumn(HAService::node)
        content.add(table)

        filter.addClickListener {
            if (it.source.value) {
                table.setItems(haproxy.getStats().filter { !it.isUp() })
            } else {
                table.setItems(haproxy.getStats())
            }
        }

    }


}