package thaumcraft.common.registry;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thaumcraft.Thaumcraft;
import thaumcraft.common.research.AspectPoolData;
import thaumcraft.common.research.ResearchKnowledgeData;

public final class TCDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Thaumcraft.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AspectPoolData>> ASPECT_POOL =
            REGISTRY.register("aspect_pool", () -> AttachmentType.builder(AspectPoolData::starter)
                    .serialize(AspectPoolData.CODEC, pool -> !pool.aspects().isEmpty())
                    .copyOnDeath()
                    .sync(AspectPoolData.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ResearchKnowledgeData>> RESEARCH_KNOWLEDGE =
            REGISTRY.register("research_knowledge", () -> AttachmentType.builder(() -> ResearchKnowledgeData.EMPTY)
                    .serialize(ResearchKnowledgeData.CODEC, knowledge -> !knowledge.completed().isEmpty())
                    .copyOnDeath()
                    .sync(ResearchKnowledgeData.STREAM_CODEC)
                    .build());

    private TCDataAttachments() {
    }
}
