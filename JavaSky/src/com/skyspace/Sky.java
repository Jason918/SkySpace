package com.skyspace;

import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Sky {
	/**
	 * singleton...
	 */
	private static Sky ts = null;
	public static Sky getInstance() {
		if (ts == null) ts = new Sky();
		return ts;
	}
	
	/**
	 * used to make it easier to log ...
	 */
	public static Logger logger = Logger.getLogger(Sky.class.getName()); 
	
	private ItemPool envItemPool;
	private ItemPool envItemCache;
	private TemplatePool envGroupCache; // store other's request.
	private TemplatePool envGroupPool;	//store own's envGroup
//	private ArrayList<MatchItem> judgeList;//??? not used 
	private NetWorker networker;
	private Timer cleaner;
	

	
	/**
	 * 构造器,做初始化工作. 
	 * 
	 */
	private Sky() {
		networker = new NetWorker();
		envItemPool = new ItemPool();
		envItemCache = new ItemPool();
		envGroupCache = new TemplatePool();
		envGroupPool = new TemplatePool();
		
	}

	/**
	 * write an Item into ENV
	 * @param ei
	 */
	public void write(Item ei) {
		envItemSended ++;
		//handle old groups.
		if (!handleNewEnvItem(ei, true)) {
			Sky.logger.fine("adding Item into pool:"+ei);
			envItemPool.add(ei);//did not acquired
		}
	}

	/**
	 * send an Template into ENV
	 * @param eg
	 */
	public void sendRequest(Template eg) {
		envGroupSended ++;
		envGroupPool.add(eg);
		networker.sendDataToEnviroment(eg.pack());
	}
	
	/**
	 * function for handling requst received from other node.
	 * @param eg
	 */
	public void handleRequest(Template eg) {
		envGroupReceived++;
		Sky.logger.entering("ENV", "handle_request", eg);
		Item ei = envItemPool.getMatch(eg);
		if (ei == null) {//not in pool ,trying cache.
			Sky.logger.fine("no match in envItemPool");
			ei = envItemCache.getMatch(eg);
			if (ei == null) { //not in the cache either.put it in buffer.
				Sky.logger.fine("no match in envItemCache");
				envGroupCache.add(eg);
			} else {//got a match in cache
				Sky.logger.fine("get match in envItemCache:"+ei);
				networker.sendResult(ei,eg,false);
			}
		} else {//got a match in pool
			Sky.logger.fine("get match in envItemPool:"+ei);
			if (eg.isAcquire()) {
				envItemPool.lock(ei);
				if (networker.sendResult(ei,eg,true)) {
					envItemPool.remove(ei);
				} else {
					envItemPool.unlock(ei);
				}
			} else {//subscribe do not need to know target want it
				networker.sendResult(ei,eg,true);
			}
		}
		Sky.logger.exiting("ENV", "handle_request");
	}
	
	/**
	 * function for handling result of request it send out.
	 * @param ei
	 * @return 
	 */
	public boolean handleResult(Item ei,Template eg) {
		envItemReceived ++;
		Sky.logger.info("get Item "+ ei + " for Template:"+eg);
		Template neg = envGroupPool.get(eg);
		if (neg != null) {
			Sky.logger.info("####################\nEnvGroup get a match:\n"
						+ neg + "\n-----\n" + ei+"\n#######################\n");
			//System.out.println(neg.owner.getAction());
			if (neg.isMany()) {
				envGroupPool.addMatch(ei,neg);
			} else {
				Template.default_callback.callback(neg,ei);
				envGroupPool.remove(neg);
			}
			envItemCache.add(ei);
			handleNewEnvItem(ei, false);
			return true;
		}
		Sky.logger.finer("do not have this Template any more");
		return false;
	}
	/**
	 * 处理新加入EnvItem的情况
	 * @param ei
	 * @param isFromOwner
	 * @return 返回新加入的是否被成功acquire了
	 */
	private boolean handleNewEnvItem(Item ei,boolean isFromOwner) {
		Collection<Template> egs = envGroupCache.getMatch(ei);
		Template  ac = null;
		for(Template eg:egs) {
			if (!eg.isAcquire()) {
				if (!networker.sendResult(ei, eg, true) && !eg.isMany()) {
					envGroupCache.add(eg);//target do not want this to end.
				}
			} else {
				if (ac == null || eg.prior(ac)) {
					ac = eg;
				} else {
					if (!eg.isMany()) {
						envGroupCache.add(eg);
					}
				}
			}
		}
		if (ac != null) {
			if (networker.sendResult(ei, ac, true)) {
				return true;
			} else if (!ac.isMany()) {
				envGroupCache.add(ac);
			}
		}
		return false;
	}

	/**
	 * eg获取的是ei的cache，需要向ei的owner发出请求
	 * @param ei
	 * @param eg
	 */
	public void handleCacheAcquireResult(Item ei, Template eg) {
		Template neg = envGroupPool.get(eg);
		if (neg != null) {		
			Item new_ei = networker.acquireEnvItem(ei, neg);
			if (new_ei != null) {
				handleResult(new_ei,eg);
			}
		}
	}

	public Item acquireFromPool(Item ei, Template eg) {
		Item nei = envItemPool.get(ei);
		if (nei != null)
			envItemPool.lock(nei);
		
		return nei;
	}

	public void confirmAcquire(Item ei, boolean accept) {
		if (accept) {
			envItemPool.remove(ei);
		}
	}

	/**
	 * start the tuplespace service.
	 */
	public void start(int period) {
		logger.info("START service!");
		networker.startRequestListener(this);
		networker.startResponseListener(this);
		cleaner = new Timer("Element Cleaner",true);
		cleaner.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				try {
					envItemPool.buryDead();
					envItemCache.buryDead();
					envGroupCache.buryDead();
					envGroupPool.buryDead();
				} catch (NullPointerException e) {
					System.out.println("NULL pointer here");
				}
			}
		}, 1000, period);
	}

	/**
	 * stop the tuplespace service.
	 */
	public void stop() {
		logger.info("STOP service!");
		networker.stopRequestListener();
		networker.stopResponseListener();
		cleaner.cancel();
	}
	
	private int envItemSended = 0;
	private int envItemReceived = 0;
	private int envGroupSended = 0;
	private int envGroupReceived = 0;
	public String getStatisticInfo() {
		
		return "EnvItem发送量："+envItemSended
				+"\nEnvItem接手量："+envItemReceived
				+"\nEnvGroup发送量："+envGroupSended
				+"\nEnvGroup接收量："+envGroupReceived
				;
	}

	public List<String> getEnvItemPoolInfo() {
		return envItemPool.toListString();
	}

	public List<String> getEnvItemCacheInfo() {
		return envItemCache.toListString();
	}

	public List<String> getEnvGroupCacheInfo() {
		return envGroupCache.toListString();
	}

	public List<String> getEnvGroupPoolInfo() {
		return envGroupPool.toListString();
	}

	public void clearData() {
		clearCache();
		envItemPool.clear();
		envGroupPool.clear();
		envItemReceived = 0;
		envItemSended = 0;
		envGroupSended = 0;
		envGroupReceived = 0;
	}

	public void clearCache() {
		envItemCache.clear();
		envGroupCache.clear();
	}
}
