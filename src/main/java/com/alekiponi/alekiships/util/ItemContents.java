package com.alekiponi.alekiships.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.text.MessageFormat;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.jetbrains.annotations.Range;

/**
 * A "bucketed" {@link ItemStack} storage (think {@code List<List<ItemStack>>})
 */
public class ItemContents implements INBTSerializable<CompoundTag> {

    public static final String SIZE_KEY = "Size";
    public static final String BUCKETS_KEY = "Buckets";
    public static final String BUCKET_KEY = "Bucket";

    private NonNullList<ItemStackBucket> contents;

    @SuppressWarnings("unused")
    public ItemContents() {
        this(1);
    }

    /**
     * @param bucketCount The amount of buckets to have
     */
    public ItemContents(final @Range(from = 0, to = Integer.MAX_VALUE) int bucketCount) {
        this.contents = NonNullList.createWithCapacity(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            this.contents.add(new ItemStackBucket());
        }
    }

    /**
     * @param bucketIndex The bucket to insert into
     * @param insertStack The insert Stack. Unmodified
     *
     * @return Insertion remainder
     *
     * @apiNote This behaves like {@link net.neoforged.neoforge.items.IItemHandler#insertItem(int, ItemStack, boolean)}
     */
    public ItemStack insertItem(final @Range(from = 0, to = Integer.MAX_VALUE) int bucketIndex,
            final ItemStack insertStack) {
        this.validateBucketIndex(bucketIndex);
        return ItemHandlerHelper.insertItem(this.getBucket(bucketIndex), insertStack, false);
    }

    /**
     * @param bucketIndex The bucket index to get the count of
     *
     * @return The item count in the bucket
     */
    public int getCount(final @Range(from = 0, to = Integer.MAX_VALUE) int bucketIndex) {
        this.validateBucketIndex(bucketIndex);
        return this.getBucket(bucketIndex).getTotalItems();
    }

    public void forEach(final Consumer<ItemStack> itemStackConsumer) {
        this.contents.forEach(itemStackBucket -> IntStream.range(0, itemStackBucket.getSlots())
                .mapToObj(itemStackBucket::getStackInSlot)
                .forEach(itemStackConsumer));
    }

    public void clear() {
        this.setSize(this.getBuckets());
    }

    @Override
    public CompoundTag serializeNBT(final HolderLookup.Provider provider) {
        final var compoundTag = new CompoundTag();
        compoundTag.putInt(SIZE_KEY, this.contents.size());
        final var buckets = new ListTag();
        for (int i = 0; i < this.contents.size(); i++) {
            final var bucket = this.contents.get(i).serializeNBT(provider);
            bucket.putInt(BUCKET_KEY, i);
            buckets.add(bucket);
        }

        compoundTag.put(BUCKETS_KEY, buckets);

        return compoundTag;
    }

    @Override
    public void deserializeNBT(final HolderLookup.Provider provider, final CompoundTag compoundTag) {
        this.setSize(compoundTag.contains(SIZE_KEY, Tag.TAG_INT) ? compoundTag.getInt(SIZE_KEY) : this.getBuckets());

        final var buckets = compoundTag.getList(BUCKETS_KEY, Tag.TAG_COMPOUND);
        for (int i = 0, bucketsSize = buckets.size(); i < bucketsSize; i++) {
            final var bucket = buckets.getCompound(i);
            final int bucketIndex = bucket.getInt(BUCKET_KEY);

            if (bucketIndex >= 0 && bucketIndex < this.contents.size()) {
                this.contents.get(bucketIndex).deserializeNBT(provider, bucket);
            }
        }
    }

    /**
     * @return The amount of buckets
     */
    public int getBuckets() {
        return this.contents.size();
    }

    protected ItemStackBucket getBucket(final @Range(from = 0, to = Integer.MAX_VALUE) int bucketIndex) {
        this.validateBucketIndex(bucketIndex);
        return this.contents.get(bucketIndex);
    }

    protected void setSize(final int size) {
        this.contents = NonNullList.createWithCapacity(size);
        for (int i = 0; i < size; i++) {
            this.contents.add(new ItemStackBucket());
        }
    }

    private void validateBucketIndex(final int bucketIndex) {
        if (bucketIndex < 0 || bucketIndex >= this.contents.size()) {
            throw new RuntimeException(
                    MessageFormat.format("Bucket {0} not in valid range - [0,{1})", bucketIndex, this.contents.size()));
        }
    }

    protected static final class ItemStackBucket extends ItemStackHandler {

        public static final int STALE_TOTAL_ITEMS = -1;
        private int totalItems = STALE_TOTAL_ITEMS;

        @Override
        public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
            var remainder = super.insertItem(slot, stack, simulate);
            while (!remainder.isEmpty()) {
                final var original = this.stacks;
                this.setSize(this.getSlots() + 1);
                IntStream.range(0, original.size())
                        .forEach(i -> this.setStackInSlot(i, this.stacks.get(i)));
                remainder = super.insertItem(slot, remainder, simulate);
            }
            return remainder;
        }

        @Override
        protected void onContentsChanged(final int slot) {
            super.onContentsChanged(slot);
            this.totalItems = STALE_TOTAL_ITEMS;
        }

        public int getTotalItems() {
            if (this.totalItems == STALE_TOTAL_ITEMS) {
                return this.totalItems = this.stacks.stream().mapToInt(ItemStack::getCount).sum();
            }
            return this.totalItems;
        }
    }
}