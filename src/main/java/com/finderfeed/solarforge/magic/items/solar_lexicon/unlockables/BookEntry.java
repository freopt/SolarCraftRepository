package com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables;

import com.finderfeed.solarforge.magic.items.solar_lexicon.achievements.Progression;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;


@Deprecated
public enum BookEntry {
    BEGINNING_INFO(tx("solar_category.beginning"),point(20,40),null,null),
    SOLAR_FORGE_BASICS(tx("solar_category.solar_forge"),point(210,40), Progression.ENTER_NETHER,null),
    ARMOR(tx("solar_category.armor"),point(20,80), Progression.SOLAR_INFUSER,null),
    STRUCTURES(tx("solar_category.structures"),point(20,700), Progression.SOLAR_INFUSER,null),

    BEGINNER(tx("solar_category.beginner"),point(20,220), Progression.SOLAR_INFUSER,null),
    BEGINNER_ITEMS(tx("solar_category.beginner_items"),point(180,260), Progression.SOLAR_INFUSER,BEGINNER),

    SKILLED(tx("solar_category.skilled"),point(20,400), Progression.IMBUED_COLD_STAR,null),
    SKILLED_ITEMS(tx("solar_category.skilled_items"),point(180,260), Progression.IMBUED_COLD_STAR,SKILLED),
    SKILLED_MATERIALS(tx("solar_category.skilled_materials"),point(180,260), Progression.IMBUED_COLD_STAR,SKILLED),

    MASTER(tx("solar_category.master"),point(20,560), Progression.CRAFT_SOLAR_LENS,null),
    MASTER_ITEMS(tx("solar_category.master_items"),point(180,260), Progression.CRAFT_SOLAR_ENERGY_GENERATOR,MASTER),
    MASTER_ENERGY(tx("solar_category.master_energy"),point(180,260), Progression.CRAFT_SOLAR_ENERGY_GENERATOR,MASTER),
    MASTER_MATERIALS(tx("solar_category.master_materials"),point(180,260), Progression.CRAFT_SOLAR_ENERGY_GENERATOR,MASTER),

    UPGRADES(tx("solar_category.upgrades"),point(210,80), Progression.CRAFT_SOLAR_ENERGY_GENERATOR,null)
    ;


    public static Map<BookEntry, List<BookEntry>> ENTRY_TREE = new HashMap<>();


    private final TranslatableComponent translation;
    private final Point placeInBook;
    private final Progression toUnlock;
    private final BookEntry child;

    BookEntry(TranslatableComponent translation, Point placeInBook, @Nullable Progression toUnlock, @Nullable BookEntry child){
        this.placeInBook = placeInBook;
        this.translation = translation;
        this.toUnlock = toUnlock;
        this.child = child;
    }


    public TranslatableComponent getTranslation() {
        return translation;

    }

    public BookEntry getParent(){
        return child;
    }

    public Point getPlaceInBook() {
        return placeInBook;
    }

    public static BookEntry[] getAllEntries(){
        return BookEntry.class.getEnumConstants();
    }

    public Progression toUnlock() {
        return toUnlock;
    }

    public static Point point(int a,int b){
        return new Point(a,b);
    }

    public static TranslatableComponent tx(String a){
        return new TranslatableComponent(a);
    }

    public static List<BookEntry> nonChildEntries(){
        List<BookEntry> list = new ArrayList<>();
        for (BookEntry entry : getAllEntries()){
            if (entry.child == null){
                list.add(entry);
            }
        }
        return list;
    }


    public static void initMap(){
        for (BookEntry entry : getAllEntries()){
            if (entry.child != null){
                if (ENTRY_TREE.containsKey(entry.child)){
                    ENTRY_TREE.get(entry.child).add(entry);
                }else{
                    List<BookEntry> list = new ArrayList<>();
                    list.add(entry);
                    ENTRY_TREE.put(entry.child,list);
                }
            }else{
                ENTRY_TREE.put(entry,new ArrayList<>());
            }
        }
    }
}
