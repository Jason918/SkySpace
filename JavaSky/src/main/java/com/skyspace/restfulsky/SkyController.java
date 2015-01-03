package com.skyspace.restfulsky;

import com.skyspace.ISkyEntry;
import com.skyspace.SkyEntry;
import com.skyspace.element.Item;
import com.skyspace.element.Template;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by jason on 14-9-14.
 */
@RestController
public class SkyController {
    public static Logger LOG = org.slf4j.LoggerFactory.getLogger(SkyController.class);

    ISkyEntry entry;

    @PostConstruct
    void init() {
        entry = new SkyEntry("restsky");
    }

    @RequestMapping("/skyentry/write")
    public Boolean write(
            @RequestParam(value = "type", required = false, defaultValue = "-1") int type,
            @RequestParam(value = "content", required = true, defaultValue = "") String tuple,
            @RequestParam(value = "expire", required = false, defaultValue = "30000") int expire) {
        LOG.info("write,[type={}] [tuple={}] [expire={}] ", type, tuple, expire);
        if (type == -1) {
            type = Item.TYPE_ACQUIRABLE|Item.TYPE_SUBSCRIBALE;
        }
        Item item = new Item(null, type, tuple, expire);
        entry.write(item);
        LOG.info("write finish");
        return true;
    }

    @RequestMapping("/skyentry/read")
    public List<Item> read(@RequestParam(value = "isMulti", required = false, defaultValue = "false") boolean isMulti,
                           @RequestParam(value = "content", required = true, defaultValue = "") String template,
                           @RequestParam(value = "timeout", required = false, defaultValue = "500") int timeout) {
        LOG.info("read,[isMulti={}] [template={}] [timeout={}] ", isMulti, template, timeout);
        int type = Template.TYPE_SUBSCRIBE;
        if (isMulti) {
            type |= Template.TYPE_MANY;
        }
        Template tmpl = new Template(null, template, type, timeout);
        List<Item> items = entry.read(tmpl);
        LOG.info("read finish");
        return items;
    }

    @RequestMapping("/skyentry/take")
    public List<Item> take(@RequestParam(value = "isMulti", required = false, defaultValue = "false") boolean isMulti,
                           @RequestParam(value = "content", required = true, defaultValue = "") String template,
                           @RequestParam(value = "timeout", required = false, defaultValue = "30000") int timeout) {
        LOG.info("take,[isMulti={}] [template={}] [timeout={}] ", isMulti, template, timeout);
        int type = Template.TYPE_ACQUIRE;
        if (isMulti) {
            type |= Template.TYPE_MANY;
        }
        Template tmpl = new Template(null, template, type, timeout);
        List<Item> items = entry.take(tmpl);
        LOG.info("take finish");
        return items;
    }
}
