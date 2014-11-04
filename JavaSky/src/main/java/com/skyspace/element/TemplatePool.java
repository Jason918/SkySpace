package com.skyspace.element;

import com.skyspace.Sky;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TemplatePool extends ElementPool {
    private CopyOnWriteArrayList<Template> tmplList = new CopyOnWriteArrayList<Template>();
    private CopyOnWriteArrayList<MatchItem> matchList;

    /**
     * 获取能够的匹配ei的EnvGroup，将返回的数据从envGroupList中删除（保留具有many属性的）
     *
     * @param it
     * @return 匹配的集合。如果没有结果就返回空的集合（非null）
     */
    public Collection<Template> getMatch(Item it) {
        CopyOnWriteArrayList<Template> tmpls = new CopyOnWriteArrayList<Template>();
        for (Template tmpl : tmplList) {
            if (tmpl.match(it)) {
                if (!tmpl.isMany())
                    tmplList.remove(tmpl);
                tmpls.add(tmpl);
            }
        }
        return tmpls;
    }

    @Override
    public void add(Element e) {
        if (e instanceof Template == false) {
            Sky.logger.warning("element is not Item");
            return;
        }
        tmplList.add((Template) e);
        e.setContainer(this);
    }

    /**
     * put an ei into a type-many tmpl;
     *
     * @param it
     * @param tmpl
     */
    public void addMatch(Item it, Template tmpl) {
        if (matchList == null)
            matchList = new CopyOnWriteArrayList<TemplatePool.MatchItem>();
        MatchItem mi = new MatchItem(tmpl);
        int index = matchList.indexOf(mi);
        if (index >= 0) {
            matchList.get(index).add(it);
        } else {
            mi.add(it);
            matchList.add(mi);
        }

    }

    @Override
    public void remove(Element e) {
        if (e instanceof Template == false) {
            Sky.logger.warning("element is not Item");
            return;
        }
        Template tmpl = (Template) e;
        tmplList.remove(tmpl);
        if (tmpl.isMany() && matchList != null) {
            int index = matchList.indexOf(tmpl);
            if (index >= 0) {
                MatchItem mi = matchList.get(index);
                mi.tmpl.getCallback().handleMany(mi.tmpl, mi.itList);
                matchList.remove(index);
            }
        }

    }

    public Template get(Template tmpl) {
        int index = tmplList.indexOf(tmpl);
        if (index >= 0)
            return tmplList.get(index);
        else
            return null;
    }

    @Override
    public void buryDead() {
        for (Template tmpl : tmplList) {
            tmpl.isAlive();
        }
    }

    public void clear() {
        tmplList.clear();
        if (matchList != null) matchList.clear();
    }

    public List<String> toListString() {
        ArrayList<String> list = new ArrayList<String>();
        for (Template tmpl : tmplList) {
            list.add(tmpl.toString());
        }
        return list;
    }

    /**
     * Class for storing a list of EnvItems which can match tmpl.
     * ei can decide to match which one.
     *
     * @author jason
     */
    class MatchItem {
        Template tmpl;
        ArrayList<Item> itList;

        MatchItem(Template tmpl2) {
            tmpl = tmpl2;
        }

        @Override
        public String toString() {
            return "MatchItem;" + tmpl + "result:" + itList;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MatchItem) {
                return tmpl.equals(((MatchItem) obj).tmpl);
            } else
                return false;
        }

        void add(Item ei) {
            if (itList == null) itList = new ArrayList<Item>();
            itList.add(ei);
        }
    }
}
