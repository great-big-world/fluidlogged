package dev.creoii.greatbigworld.fluidlogged.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

public class WaterloggingDataFix extends DataFix {
    public WaterloggingDataFix(Schema outputSchema) {
        super(outputSchema, false);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return fixTypeEverywhereTyped("WaterloggingStateFix", getInputSchema().getType(References.BLOCK_STATE), input -> input.update(DSL.remainderFinder(), dynamic -> dynamic.update("Properties", properties -> {
            Optional<? extends Dynamic<?>> dataResult = properties.get("fluid").result();
            if (dataResult.isPresent()) {
                properties = properties.remove("fluid");
                String value = dataResult.get().asString("empty");
                if ("water".equals(value)) {
                    return properties.set("waterlogged", properties.createBoolean(true));
                } else {
                    return properties.set("waterlogged", properties.createBoolean(false));
                }
            }
            return properties;
        })));
    }
}
