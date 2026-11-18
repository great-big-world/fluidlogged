package dev.creoii.greatbigworld.fluidlogged.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

public class FluidloggingDataFix extends DataFix {
    public FluidloggingDataFix(Schema outputSchema) {
        super(outputSchema, false);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        TypeRewriteRule flatBlockState = fixTypeEverywhereTyped(
                "FluidloggingFlatBlockStateFix",
                getInputSchema().getType(References.CHUNK),
                input -> {
                    System.out.println("Processing block state: " + input);
                    return input.updateRecursive(DSL.remainderFinder(), dynamic -> dynamic.update("properties", properties -> {
                        Optional<? extends Dynamic<?>> dataResult = properties.get("waterlogged").result();
                        System.out.println("fixing: " + properties.toString());
                        if (dataResult.isPresent()) {
                            properties = properties.remove("waterlogged");
                            System.out.println("removed: " + properties.toString());
                            boolean value = dataResult.get().asBoolean(false);
                            if (value) {
                                System.out.println("set to water fluid");
                                return properties.set("fluidlogged", properties.createString("water"));
                            } else {
                                System.out.println("set to empty fluid");
                                return properties.set("fluidlogged", properties.createString("empty"));
                            }
                        }
                        return properties;
                    }));
                }
        );

        return flatBlockState;
    }
}
