package io.ejekta.kambrikx.internal.serial.decoders

import io.ejekta.kambrikx.api.serial.nbt.NbtFormatConfig
import io.ejekta.kambrikx.internal.serial.encoders.BaseTagEncoder
import io.ejekta.kambrikx.internal.serial.encoders.TagClassEncoder
import io.ejekta.kambrikx.internal.serial.encoders.TagListEncoder
import io.ejekta.kambrikx.internal.serial.encoders.TagMapEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import net.minecraft.nbt.*

@InternalSerializationApi
open class TagDecoder(
    config: NbtFormatConfig,
    level: Int,
    final override var root: Tag
) : BaseTagDecoder(config, level) {
    private var position = 0

    override fun readTag(name: String): Tag {
        return EndTag.INSTANCE
    }

    @ExperimentalSerializationApi
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return CompositeDecoder.DECODE_DONE
    }

}

@InternalSerializationApi
open class TagClassDecoder(
    config: NbtFormatConfig,
    level: Int,
    override var root: Tag
) : BaseTagDecoder(config, level) {
    private var position = 0

    private val tagCompound: CompoundTag
        get() = root as CompoundTag

    override fun readTag(name: String): Tag {
        return tagCompound.get(name)!!
    }

    @ExperimentalSerializationApi
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (position < descriptor.elementsCount) {
            val name = descriptor.getTag(position++)
            if (name in tagCompound) {
                return position - 1
            }
        }
        return CompositeDecoder.DECODE_DONE
    }

}

@InternalSerializationApi
@ExperimentalSerializationApi
open class TagListDecoder(
    config: NbtFormatConfig,
    level: Int,
    override var root: Tag
) : BaseTagDecoder(config, level) {

    private val tagList: ListTag
        get() = root as ListTag

    private val size = tagList.size
    private var currentIndex = -1

    override fun readTag(name: String) = tagList[name.toInt()]!!

    override fun elementName(desc: SerialDescriptor, index: Int): String = index.toString()

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (currentIndex < size - 1) {
            currentIndex++
            return currentIndex
        }
        return CompositeDecoder.DECODE_DONE
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        // do nothing, maps do not have strict keys, so strict mode check is omitted
    }

}

@InternalSerializationApi
@ExperimentalSerializationApi
open class TagMapDecoder(
    config: NbtFormatConfig,
    level: Int,
    override var root: Tag
) : BaseTagDecoder(config, level) {

    private val tagCompound: CompoundTag
        get() = root as CompoundTag

    private val keys = tagCompound.keys.toList()
    private val size: Int = keys.size * 2
    private var position = -1

    override fun readTag(name: String): Tag = if (position % 2 == 0) StringTag.of(name) else tagCompound.get(name)!!


    override fun elementName(desc: SerialDescriptor, index: Int): String = keys[index / 2]

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (position < size - 1) {
            position++
            return position
        }
        return CompositeDecoder.DECODE_DONE
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        // As per KSX: do nothing, maps do not have strict keys, so strict mode check is omitted
    }

}