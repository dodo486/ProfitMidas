package bulls.designTemplate;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface JsonConvertible {
    default ObjectNode toObjectNode() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        fillObjectNode(node);
        return node;
    }

    void fillObjectNode(ObjectNode node);
}
