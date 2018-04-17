package com.smartions.dabolo.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.smartions.dabolo.model.Activity;
@Service
public class ActivityService implements IActivityService {

	@Override
	public List<Activity> getActivityList() {
		List<Activity> list = new ArrayList<Activity>();
		Activity activity1 = new Activity();
		activity1.setId(1);
		activity1.setCoverpath( "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=922299216,1061717478&fm=200&gp=0.jpg");
		activity1.setEndtime("2018-5-12");
		activity1.setStarttime("2018-5-11");
		activity1.setQty(20);
		activity1.setTitle("5月1日大学同学聚会");
		activity1.setStatus("报名中");
		activity1.setAddress("江苏省苏州市工业园区创意产业园");
		list.add(activity1);
		
		Activity activity2 = new Activity();
		activity2.setId(1);
		activity2.setCoverpath("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524472257&di=8142652bde90d3ddcd7207e6dcfe87ba&imgtype=jpg&er=1&src=http%3A%2F%2Fscimg.jb51.net%2Fallimg%2F140730%2F11-140I01132494T.jpg");
		activity2.setStarttime("2018-5-11");
		activity2.setQty(40);
		activity2.setTitle("5月12日打篮球");
		activity2.setStatus("进行中");
		activity2.setAddress("江苏省苏州市工业园区");
		list.add(activity2);
		return list;
	}

	@Override
	public Activity getActivityInfo(long id) {
		Activity activity2 = new Activity();
		activity2.setId(1);
		activity2.setCoverpath("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524472257&di=8142652bde90d3ddcd7207e6dcfe87ba&imgtype=jpg&er=1&src=http%3A%2F%2Fscimg.jb51.net%2Fallimg%2F140730%2F11-140I01132494T.jpg");
		activity2.setStarttime("2018-5-11");
		activity2.setEndtime("2018-5-12");
		activity2.setQty(40);
		activity2.setTitle("5月12日打篮球");
		activity2.setStatus("进行中");
		activity2.setAddress("江苏省苏州市工业园区");
		return activity2;
	}

}
