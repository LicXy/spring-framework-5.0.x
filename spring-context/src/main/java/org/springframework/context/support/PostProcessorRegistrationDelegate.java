/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	/**
	 * 1. 执行自定义的实现BeanDefinitionRegistryPostProcessor接口时重写的postProcessBeanDefinitionRegistry()方法
	 *
	 * 2. 执行Spring内部的实现BeanDefinitionRegistryPostProcessor接口时重写的postProcessBeanDefinitionRegistry()方法
	 *  {@link PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors(Collection, BeanDefinitionRegistry)}
	 *
	 * 3. 执行自定义的实现BeanDefinitionRegistryPostProcessor接口和BeanFactoryPostProcessor接口时重写的postProcessBeanFactory()方法
	 * 	 和Spring内部的实现BeanDefinitionRegistryPostProcessor接口时重写的postProcessBeanFactory()方法
	 *
	 * 4. 执行Spring内部实现BeanFactoryPostProcessor接口的处理器中的postProcessBeanFactory()方法
	 * {@link PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(Collection, ConfigurableListableBeanFactory)}
	 *
	 *
	 */
	public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		Set<String> processedBeans = new HashSet<>();

		if (beanFactory instanceof BeanDefinitionRegistry) {
			//注意: 前提条件是改注册器是实现了BeanDefinitionRegistry接口的
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

			/**
			 *  1. 将自定义的实现BeanDefinitionRegistryPostProcessor接口和BeanFactoryPostProcessor接口的类区分保存在不同的集合中
			 *     在区分过程中执行自定义实现BeanDefinitionRegistryPostProcessor接口时重写的postProcessBeanDefinitionRegistry()方法
			 */

			/**
			 * 注意: BeanDefinitionRegistryPostProcessor接口 继承了 BeanFactoryPostProcessor接口
			 * 之所以要将所有的后处理器区分开, 是因为实现BeanDefinitionRegistryPostProcessor接口的要执行两个方法
			 * {@link BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry(BeanDefinitionRegistry)}
			 * 和{@link BeanFactoryPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)}这两个方法
			 * 而实现BeanFactoryPostProcessor接口的只需要执行{@link BeanFactoryPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)}
			 * 这个方法即可
			 */
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			/**
			 * 遍历bean工厂后处理器集合,将不同的处理器添加分别添加到regularPostProcessors和registryProcessors集合中
			 * beanFactoryPostProcessors集合中存储的是用户自定义的后处理器类
			 * 有两种方式自定义:
			 * 		(1). 实现BeanDefinitionRegistryPostProcessor接口重写postProcessBeanDefinitionRegistry()方法和postProcessBeanFactory()方法, 还需要加@Component注解
			 * 		(2). 实现BeanFactoryPostProcessor接口重写postProcessBeanFactory()方法, 还需要加@Component注解
			 */
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =  (BeanDefinitionRegistryPostProcessor) postProcessor;
					//执行自定义实现BeanDefinitionRegistryPostProcessor接口的类中的postProcessBeanDefinitionRegistry()方法
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					registryProcessors.add(registryProcessor);
				}
				else {
					regularPostProcessors.add(postProcessor);
				}
			}


			/**
			 *  2. 将Spring内部的实现BeanDefinitionRegistryPostProcessor接口的根据优先级顺序执行 处理器类中重写该接口的postProcessBeanDefinitionRegistry()方法
			 *  (实现PriorityOrdered接口的先执行,实现ordered接口的后执行,两者都没有实现的,最后执行)
			 */

			/**
			 * 将实现PriorityOrdered,Ordered等接口的BeanDefinitionRegistryPostProcessors分开按优先级执行
			 * 这个currentRegistryProcessors 存储的是spring内部自己实现了BeanDefinitionRegistryPostProcessor接口的对象
			 */
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			/**
			 * 2.1 首先，调用实现PriorityOrdered的BeanDefinitionRegistryPostProcessor
			 *     根据BeanDefinitionRegistryPostProcessor.class获取所有实现BeanDefinitionRegistryPostProcessor接口的beanName;
			 *     getBeanNamesForType()
			 *    {@link DefaultListableBeanFactory#getBeanNamesForType(java.lang.Class, boolean, boolean)}
			 */
			String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			/**
			 *  这个地方可以得到一个BeanFactoryPostProcessor(org.springframework.context.annotation.internalConfigurationAnnotationProcessor ->内部配置注解处理器)，
			 *  因为是spring默认在最开始自己注册的,在下面的方法中注册的
			 * {@link AnnotationConfigUtils#registerAnnotationConfigProcessors()}
			 *
			 * 	为什么要在最开始注册这个呢？
			 * 因为spring的工厂需要许解析去扫描等等功能,这些功能都是需要在spring工厂初始化完成之前执行
			 * 要么在工厂最开始的时候、要么在工厂初始化之中，反正不能再之后; 因为如果在之后就没有意义，因为那个时候已经需要使用工厂了
			 * 所以这里spring在一开始就注册了一个BeanFactoryPostProcessor，用来插手springfactory的实例化过程
			 * 在这个地方断点可以知道这个类叫做ConfigurationClassPostProcessor
			 * (ConfigurationClassPostProcessor类的beanName为"org.springframework.context.annotation.internalConfigurationAnnotationProcessor")
			 *
			 * ConfigurationClassPostProcessor那么这个类能干嘛呢？    ----> 很重要
			 * Spring使用ConfigurationClassPostProcessor完成对注解的扫描
			 * (1) ConfigurationClassPostProcessor调用postProcessBeanDefinitionRegistry()方法
			 *    {@link PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors(Collection, BeanDefinitionRegistry)}
			 * (2) 在步骤四中ConfigurationClassPostProcessor调用postProcessBeanFactory()方法
			 *    {@link PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(Collection, BeanDefinitionRegistry)}
			 *
			 */
			for (String ppName : postProcessorNames) {
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					//即将执行的BeanDefinitionRegistryPostProcessor类
					processedBeans.add(ppName);
				}
			}
			//根据PriorityOrdered接口设置的不同优先级进行排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			//将Spring内部实现PriorityOrdered接口的BeanDefinitionRegistryPostProcessor类实添加到registryProcessors结合中,便于后面一块执行postProcessBeanFactory()方法
			// 在下面的执行中只执行Spring内部的,自定义的在上面区分两种类时已经执行过了
			registryProcessors.addAll(currentRegistryProcessors);
			/**
			 * 循环遍历currentRegistryProcessors,.执行BeanDefinitionRegistryPostProcessor中的postProcessBeanDefinitionRegistry方法
			 * {@link BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry(BeanDefinitionRegistry)}
			 * 该方法时BeanDefinitionRegistryPostProcessor接口继承BeanFactoryPostProcessor接口后增加的方法
			 */
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			//currentRegistryProcessors集合中的类遍历执行完postProcessBeanDefinitionRegistry()方法后,清空该集合,以便后面进行重复使用
			currentRegistryProcessors.clear();


			/**
			 * 2.2  接下来，调用实现Ordered的BeanDefinitionRegistryPostProcessor
			 */
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				// !processedBeans.contains(ppName) 如果processedBeans集合中已存在该beanName,那么说明已经执行过了,不需要再次执行
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					//把符合条件的BeanDefinition从从工厂中获取出来, 存储到currentRegistryProcessors集合中
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					//即将执行的BeanDefinitionRegistryPostProcessor类
					processedBeans.add(ppName);
				}
			}
			//根据Ordered接口设置的不同优先级进行排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			//将Spring内部实现Ordered接口的BeanDefinitionRegistryPostProcessor类实添加到registryProcessors结合中,便于后面一块执行postProcessBeanFactory()方法
			//在下面的执行中只执行Spring内部的,自定义的在上面区分两种类时已经执行过了
			registryProcessors.addAll(currentRegistryProcessors);
			//同上, 优先执行实现PriorityOrdered接口的BeanDefinitionRegistryPostProcessor, 然后再执行实现Ordered接口的
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			/**
			 * 2.3  最后，执行其他没有实现PriorityOrdered接口和ordered接口的注册器后处理器类
			 */
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					if (!processedBeans.contains(ppName)) {
						//把符合条件的BeanDefinition从从工厂中获取出来, 存储到currentRegistryProcessors集合中
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				//将Spring内部实现Ordered接口的BeanDefinitionRegistryPostProcessor类实添加到registryProcessors结合中,便于后面一块执行postProcessBeanFactory()方法
				registryProcessors.addAll(currentRegistryProcessors);
				//同上, 该步骤执行没有实现PriorityOrdered接口和Ordered接口的处理器类的BeanDefinitionRegistryPostProcessor()方法
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}

			/**
			 * 3. 执行所有处理器类(实现BeanDefinitionRegistryPostProcessor接口和BeanFactoryPostProcessor接口的)的BeanFactoryPostProcessor方法
			 *    因为BeanDefinitionRegistryPostProcessor继承BeanFactoryPostProcessor接口,所以实现BeanDefinitionRegistryPostProcessor接口的处理器类也需要
			 *    重写BeanFactoryPostProcessor接口中的BeanFactoryPostProcessor方法
			 *
			 *    注意: registryProcessors中存储的是Spring内部和自定义实现BeanDefinitionRegistryPostProcessor接口的处理器类
			 *          regularPostProcessors中存储的是自定义实现BeanFactoryPostProcessor接口的处理器类
			 */
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}

		// 如果该注册器是没有实现BeanDefinitionRegistry接口
		else {
			// 调用用上下文实例注册的工厂处理器
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}


		/**
		 * 4. 执行Spring内部实现BeanFactoryPostProcessor接口的处理器中的postProcessBeanFactory()方法
		 * 根据BeanFactoryPostProcessor类型获取到所有Spring内部实现BeanFactoryPostProcessor接口的处理器类的beanName
		 * {@link DefaultListableBeanFactory#getBeanNamesForType(java.lang.Class, boolean, boolean)}
		 */
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		//初始化存储实现PriorityOrdered接口的处理器的集合
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		//初始化存储实现Ordered接口的BeanName的集合,后面还需要再次遍历该集合, 获取BeanDefinition存储到orderedPostProcessors集合中
		List<String> orderedPostProcessorNames = new ArrayList<>();
		//初始化存储没有实现PriorityOrdered接口和Ordered接口的BeanName的集合,后面还需要再次遍历该集合, 获取BeanDefinition存储到nonOrderedPostProcessors集合中
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();

		//遍历postProcessorNames集合中所有的postProcessorName,从bean工厂中获取到相关的bean定义,存储在不同的集合中
		for (String ppName : postProcessorNames) {
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
	            //注意此处存储的是beanName;
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		/**
		 *  4.1 执行实现PriorityOrdered接口的BeanFactoryPostProcessors处理器类
		 */
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		/**
		 * 4.2  遍历orderedPostProcessorNames集合, 获取BeanDefinition存储到orderedPostProcessors集合中
		 *      然后执行实现Ordered接口的BeanFactoryPostProcessors处理器类
		 */
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		/**
		 * 4.3  遍历nonOrderedPostProcessorNames集合, 获取BeanDefinition存储到nonOrderedPostProcessors集合中
		 */
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		//清除缓存的合并bean定义，因为后处理器可能有修改原始元数据，例如替换值中的占位符…
		beanFactory.clearMetadataCache();

	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		/**
		 * BeanPostProcessorChecker是一个普通的信息打印,但是会出现一些请况:
		 * 当Spring的配置中的后处理器还没有被注册就已经开始了bean的初始化时,便会打印出BeanPostProcessorChecker中设定的信息
		 */
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		//使用priorityOrdered保证顺序
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		//使用Ordered保证顺序
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		//将bean处理器分类存储
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		//第一步,注册所有实现了PriorityOrdered接口的BeanPostProcessors
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		/**
		 * {@link org.springframework.beans.factory.support.AbstractBeanFactory#addBeanPostProcessor(BeanPostProcessor)}
		 */
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		//第二步,注册所有实现Ordered的BeanPostProcessors
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		//第三步,注册所有无序的BeanPostProcessor
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		//第四步,注册所有MergedBeanDefinitionPostProcessors类型的BeanPostProcessor,并非重复注册
		//在beanFactory.addBeanPostProcessor中会先移除已经存在的BeanPostProcessor
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		//添加ApplicationListener探测器
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			/**
			 * <-----  注解模式下   ----->
			 * 当postProcessor为ConfigurationClassPostProcessor时, 执行
			 * {@link org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry}
			 * 上面的方法中完成了注解类中的扫描和注册
			 */
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		/**
		 * <-----  注解模式下   ----->
		 * 当postProcessor为ConfigurationClassPostProcessor时, 执行
		 * {@link ConfigurationClassPostProcessor#postProcessBeanFactory}
		 * 完成对配置类的代理
		 */

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
