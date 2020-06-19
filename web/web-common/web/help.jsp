<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-help.js" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:maincontent title="Scenario Explorer: An Anticipatory Thinking Platform">
	<imaginationsupport:maincontentsection>

		<h2 class="mb-3 text-primary" id="top">QUICK START USER GUIDE</h2>

		<imaginationsupport:collapsiblesection
			title="Welcome"
			bodyId="welcome"
			initiallyExpanded="true"
			headerCssClasses="h3 mb-3 text-primary"
			bodyCssClasses="bg-light rounded p-3 mb-3">

			<p>In this manual, you will find information that will help guide you to utilize the full capabilities of the Scenario Explorer platform. New users will benefit from the following
				sections:</p>

			<ul>
				<li>A short introduction to anticipatory thinking (AT)</li>
				<li>A summary of Scenario Explorer's technical and analytic capabilities</li>
				<li>Descriptions of key terms necessary for starting a project</li>
				<li>Step-by-step instructions on how to start a project</li>
				<li>Descriptions of analytic techniques to use in projects</li>
				<li>Scenario Explorer Glossary</li>
			</ul>
		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Introduction to Anticipatory Thinking - Avoiding Surprise"
			bodyId="introduction"
			initiallyExpanded="true"
			headerCssClasses="h3 mb-3 text-primary"
			bodyCssClasses="bg-light rounded p-3 mb-3">

		<p>Anticipatory Thinking (AT) is intentionally divergent thinking that enables a person to better foresee future events (and combinations of events) and their cascade of consequences. If we
			could always correctly predict the sequence of events that will occur, there would be little need to consider any other scenarios. However, we often view the evolution of the situation
			with limited and noisy information, so despite our best predictions, we are frequently surprised. AT is similar to prediction and forecasting because they all attempt to correctly identify
			the scenario that is evolving. However, as shown in Figure 1, prediction and forecasting tend to prioritize precision (being close to the correct answer and avoiding false alarms) while AT
			prioritizes recall (ensuring the correct scenario in the set of answers). These different priorities require different analytic techniques.</p>

		<figure class="my-3">
			<img src="img/manual-figure-1.png" alt="Figure 1" class="figure-img img-fluid rounded ml-4">
			<figcaption class="figure-caption">Figure 1. The trade-off of Precision and Recall defines a space of future-oriented analytics. On the left, we strive to give a single close answer to
				achieve high precision. While on the right, we strive to ensure the single correct answer is included.
			</figcaption>
		</figure>

		<p>Foresight allows us to better avoid being surprised and finding ourselves in a situation we had not previously considered or imagined. There are some potential sources of surprise that may
			be too challenging to foresee at all because we simply do not use a mental model that allows for such things. Others are completely foreseeable given our understanding of the situation but
			we fail to consider them for some reason. However, many situations surprise us, even if we have all of the pieces of the puzzle, simply because we had not put the piece together in our
			heads in the same way that they eventually played out in real life. This issue is amplified when the pieces are scattered between the heads of multiple people who and are simply not
			assembled into a unified picture until after the surprise has occurred. If you have the knowledge &quot;Why were you unable to foresee this as a possibility?&quot; Often the answer to this
			question lies in the complexity of:</p>

		<ul>
			<li>Talking through the potentially relevant things that you (or your group) know,</li>
			<li>Rigorously and systematically developing a set of scenarios, and</li>
			<li>Identifying which scenarios are of interest with respect to various concerns.</li>
		</ul>

		<p>Human working memory limitations and time constraints reduce us to thinking about a small sample from the range of feasible scenarios. This sample is often biased towards those things that
			we have already imagined, if not come to expect.</p>

		<div class="text-right"><a href="#top" class="nav-link text-secondary font-italic">Back to top</a></div>

		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Scenario Explorer for Anticipatory Thinking"
			bodyId="goal"
			initiallyExpanded="true"
			headerCssClasses="h3 mb-3 text-primary"
			bodyCssClasses="bg-light rounded p-3 mb-3">

		<p>The primary goal of a Scenario Explorer platform is to automate and augment the ability of analysts to conduct AT techniques interactively, reliably, and at scale. ARA's Scenario Explorer
			is being designed and prototyped to balance the cognitive load of the analyst with the computational power of modern computers.</p>

		<p>Scenario Explorer includes several novel structured analytic techniques for AT that attempt to help analysts more rigorously, efficiently, and creatively explore a wide range of feasible
			future scenarios by:</p>

		<ul>
			<li><strong>Enabling multiple analysts to work together</strong> to converge on a common model of the features and timeframes that are relevant for a given project, domain, or topic.
				<a href="#topic_scenario" class="nav-link">[see &quot;Scenario&quot;]</a>
			</li>
			<li><strong>Eliciting the known, expected, or previously imagined future events</strong> and scenarios while expressing them consistently with respect to the common model.
				<a href="#topic_futures_building" class="nav-link">[see &quot;Futures Building&quot;]</a>
			</li>
			<li><strong>Putting the analyst in a mind-set to imagine feasible future events</strong> that have significant effects on the features in the common model.
				<a href="#topic_extreme_states" class="nav-link">[see &quot;Extreme States&quot;]</a>
			</li>
			<li><strong>Combining the elicited scenarios,</strong> automatically composing the events in scenarios (potentially created by different users) to generate novel but sensible scenarios.
				<a href="#topic_tree" class="nav-link">[see &quot;Tree&quot;]</a>
			</li>
			<li><strong>Exploring manipulations to the scenarios</strong> and observing the cascade multiple orders of consequences to understand the sensitivity and uncertainty in accumulated
				knowledge.
				<a href="#topic_what_if" class="nav-link">[see &quot;What If Analysis&quot;]</a>
			</li>
			<li><strong>Intelligently querying the combined scenarios</strong> to discover key events and potential leading indicators that can determine which scenarios are likely to be occurring.
				<a href="#topic_smart_query" class="nav-link">[see &quot;Smart Query&quot;]</a>
			</li>
		</ul>

		<figure class="my-3">
			<img src="img/manual-figure-2.png" alt="Figure 2" class="figure-img img-fluid rounded ml-4">
		</figure>

		<div class="text-right"><a href="#top" class="nav-link text-secondary font-italic">Back to top</a></div>

		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Key Terms"
			bodyId="key-terms"
			initiallyExpanded="true"
			headerCssClasses="h3 mb-3 text-primary"
			bodyCssClasses="bg-light rounded p-3 mb-3">

		<p>Scenario Explorer uses AT-specific terms as part of its user interface. Learning these terms will help you create projects to analyze scenarios based on your needs. This section describes
			key terms that are important for users to understand as they build their projects (for a full glossary of Scenario Explorer terms, see the appendix of this manual):</p>

		<ul>
			<li><a href="#topic_tree">Tree</a></li>
			<li><a href="#topic_state">State</a></li>
			<li><a href="#topic_feature">Feature</a></li>
			<li><a href="#topic_projector">Projector</a></li>
			<li><a href="#topic_scenario">Scenario</a></li>
			<li><a href="#topic_conditioning_events">Conditioning Event</a></li>
			<li><a href="#topic_outcomes">Outcome</a></li>
		</ul>

		<h4 class="mt-5 mb-3 text-primary" id="topic_tree">Tree</h4>
		<figure class="my-3">
			<img src="img/manual-figure-tree.png" alt="Figure: Tree" class="figure-img img-fluid rounded ml-4">
		</figure>
		<p>Scenario Explorer represents possible futures as a tree (i.e. a fully connected graph with no cycles), where scenarios are mapped along a timeline. Example: The tree below follows an 8
			month timeline from May 2018 through January 2019.</p>

		<h4 class="mt-5 mb-3 text-primary" id="topic_state">State</h4>
		<figure class="my-3">
			<img src="img/manual-figure-state.png" alt="Figure: State" class="figure-img img-fluid rounded ml-4">
		</figure>
		<p>States are nodes in the tree that hold the values for each feature in the project. The root node of the tree, called the now state, is the starting point for all possible futures. The now
			state includes the current values for each of the features. Futures grow and branch out of the now state as a tree. Typically, Scenario Explorer draws this tree left (now or the origin
			state) to right (the future) to represent time between the project start to the project end date. Tip: The user can select any date to start building the chain of events: it is the root of
			the tree, the starting point for looking into the future. The now state for this example is May 1, 2018.</p>

		<h4 class="mt-5 mb-3 text-primary" id="topic_feature">Feature</h4>
		<figure class="my-3">
			<img src="img/manual-figure-feature.png" alt="Figure: Tree" class="figure-img img-fluid rounded ml-4">
		</figure>
		<p>Features are the key variables that analysts track as they interact with possible futures. Features have values that may change over time in response to the context--similar in concept to
			algebraic variables or logical fluents. Scenario Explorer currently supports the following feature types: Boolean, integer, multiple choice, probability, and text. Tip: Part of the
			challenge of creating a new project is for the team of analysts converge on a common set of Features that they believe balance covering the relevant information and not including extra or
			useless Features. This can be an iterative process. In the example below, there are six features in which the analyst is plotting scenarios around Mr. Chen's senate campaign and potential
			cyber interference.</p>

		<h4 class="mt-5 mb-3 text-primary" id="topic_projector">Projector</h4>
		<p>Projectors are algorithms that forecast the future value of their assigned feature based on its previous states. Projectors are applied to each state and attempt to predict the change in
			the value since the previous state. Projectors are assigned specifically to features, and the Scenario Explorer system is designed to allow Projectors to be dynamically plugged into the
			platform as they are developed. An example of a projector could be a function that adds compounding interest and this projector (compounding interest) could be assigned to a feature (i.e.
			the amount of money in one's savings account) even with no intervening events, the amount of money in the account will increase over time. If no projector is assigned then the feature
			value is propagates unchanged unless a conditioning event outcome changes it.</p>

		<h4 class="mt-5 mb-3 text-primary" id="topic_scenario">Scenario</h4>
		<figure class="my-3">
			<img src="img/manual-figure-scenario.png" alt="Figure: Scenario" class="figure-img img-fluid rounded ml-4">
		</figure>
		<p>A scenario is a trajectory through the tree of futures, following one through line from the now state to the terminal state in a potential series of events. The scenario includes all of the
			components (states, features, conditioning events, outcomes, etc.) that comprise a specific trajectory through the tree. Example: The scenario depicted below shows a specific trajectory
			from May 2018 - September 2018. It has one conditioning event in July, &quot;Hackers deface website with False Scandal&quot; and its outcome is &quot;Chen's Support Increases.&quot;</p>

		<h4 class="mt-5 mb-3 text-primary" id="topic_conditioning_events">Conditioning Event</h4>
		<figure class="my-3">
			<img src="img/manual-figure-conditioning-event.png" alt="Figure: Conditioning Event" class="figure-img img-fluid rounded ml-4">
		</figure>
		<p>Conditioning Events introduce branching to the tree of futures. A conditioning event is a situation in which there are multiple possible and mutually exclusive outcomes, each outcome
			resulting in a different effect on the feature values that exist in a future state. Example: An example of a conditioning event would be &quot;2016 Election in US&quot; as the outcome
			would have
			effects on certain features, such as President (Trump, Clinton, Stein), # Republican Senators, and Probability of ACA being repealed.</p>

		<h4 class="mt-5 mb-3 text-primary" id="topic_outcomes">Outcome</h4>
		<figure class="my-3">
			<img src="img/manual-figure-outcome.png" alt="Figure: Outcome" class="figure-img img-fluid rounded ml-4">
		</figure>
		<p>An outcome is a potential future state that results after a conditioning event. Within the outcome, the project's feature values may change based on the trajectory. Every conditioning event
			has multiple potential outcomes, each outcome has some impact on some set of features. Example: The election (conditioning event)'s outcome would be the selection of the president
			(feature).</p>

		<div class="text-right"><a href="#top" class="nav-link text-secondary font-italic">Back to top</a></div>

		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Support Section: How do Conditioning Events and Outcomes Work?"
			bodyId="support-section"
			initiallyExpanded="true"
			headerCssClasses="h3 mb-3 text-primary"
			bodyCssClasses="bg-light rounded p-3 mb-3">

		<p>Conditional forecasts are predictions of the future assuming some given condition is true:</p>
		<table>
			<tbody>
			<tr>
				<td class="text-right align-text-top">General Forecast Question:</td>
				<td class="pl-2">What will the average price of an electric car be in 2040?</td>
			</tr>
			<tr>
				<td class="text-right align-text-top">Conditional Forecast Question:</td>
				<td class="pl-2">What will the average price of an electric car be in 2040,<br /><span class="bg-warning">assuming that Tesla releases the Model 4 as advertised?</span></td>
			</tr>
			</tbody>
		</table>
		<p>The additional qualifier &quot;<i>assuming that Tesla releases the Model 4 as advertised</i>&quot; limits the scope of the forecast question because we no longer have to consider
			alternatives
			possibilities for the Tesla Model 4 release.</p>

		<h4 class="mt-3 mb-3 text-primary">Multiple Possible and Mutually Exclusive Outcomes</h4>

		<p>As shown in Figure 3, a conditioning event is a situation in which there are multiple possible and mutually exclusive outcomes, each outcomes resulting in a different effect on the values
			that would be forecast for a future state. Grouping related outcomes under a common semantic event allows us to systematically evaluate each possible outcome. By quantifying the effects
			that each outcome has on the features allows us to trace changes the state of the world (within the confines of this project) depending on which outcomes conditions it.</p>

		<p>For example, let a feature (A) represent the number of electric cars sold per day in some area. Today, we may sell 20 cars per day and we are interested in what future sales might look
			like. We identify that announcement expected next week about federal subsidies for electric cars (i.e. a conditioning event) would likely have a significant effect on that number in the
			future. We can imagine an announcement that increases incentives could cause the sales numbers to increase. An alternative outcome could be removing incentives which could have the effect
			of the sales number dropping. As such, improving the accuracy of a forecast can sometimes be dependent on identifying the conditions under which we can expect it to be valid (usually the
			assumptions under which it was analyzed and computed).</p>

		<figure class="my-3">
			<img src="img/manual-figure-3.png" alt="Figure 3" class="figure-img img-fluid rounded ml-4">
			<figcaption class="figure-caption">Figure 3. A conditioning event changes the context in which future events occur in a predictable way. A conditioning event can only occur when its
				pre-requisites are satisfied. It has a set (at least 1) possible outcomes, each that has a set of (at least 1) effects. The outcomes branch the futures tree, and their effects modify
				the values of features between the previous and next state.
			</figcaption>
		</figure>

		<div class="text-right"><a href="#top" class="nav-link text-secondary font-italic">Back to top</a></div>

		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Starting Your First Project"
			bodyId="first-project"
			initiallyExpanded="true"
			headerCssClasses="h3 mb-3 text-primary"
			bodyCssClasses="bg-light rounded p-3 mb-3">

		<p>After naming the project and defining its timeline (start and end dates), the Scenario Explorer System is flexible as to the order of steps required to create a new project. The user can
			later return to any of these steps to update their project.</p>
		<ol>
			<li><strong>Log in to Scenario Explorer.</strong>
				<p>Input your username and password to gain access to your dashboard.</p></li>
			<li><strong>Create a project.</strong>
				<p>On your Scenario Explorer dashboard, start a new project. Projects are initially given a unique name, description (which can be viewed by all), and a start and end date. The project
					is the specific project or research question that the analysts are using the platform to analyze.</p>
				<p class="font-weight-light ml-4">Tip: While optional, a description of the project is useful for providing mission-specific context to collaborating team members.</p>
			</li>
			<li><strong>Create features.</strong>
				<p>The next step in anticipatory thinking is to define the features of interest. In the Scenario Explorer system, features are assigned a feature type which defines what kind of data
					they store. Feature types are also designed to be extendable by using our API to develop new Feature Types. In creating a new Project, users identify the Features they wish to
					include in the project, give them a label, description, and any feature type configuration parameters (e.g., units). This allows a collaborative team to understand the intention of
					each feature.</p>
				<p class="font-weight-light ml-4">Tip: Part of the challenge of creating a new project is for the team of analysts converge on a common set of features that they believe balance
					covering the relevant information and not including extra or useless features. This can be an iterative process.</p></li>
			<li><strong>Create timeline events.</strong>
				<p>The third step is to identify a timeline of events that will take place regardless of which future we are in. Timeline events provide context. For example a conditioning event can
					have the pre-requisite that it only takes place within some temporal relationship to a Timeline Event (e.g., during or preceding). In Scenario Explorer, timeline events are shown
					time-aligned along the bottom of the tree display so that a user can quickly identify their relationship to the states and conditioning events in the tree.</p>
				<p class="font-weight-light ml-4">Tip: Timeline Events can also be uploaded from a .csv file.</p></li>
			<li><strong>Invite members.</strong>
				<p>The fourth step involves identifying members, users that should have access to work in the newly created project space.</p>
				<p class="font-weight-light ml-4">Tip: We are currently investigating user roles within a project (such as &quot;read-only&quot;), however at this time all invited users will have full
					access to edit features, timeline events, conditioning events, and views in the Project-space.</p>
			</li>
			<li><strong>View the Scenario.</strong>
				<p>Prior to adding any Conditioning Events, it is recommended that analysts view the scenario where no intervening events occur. You can do this by creating a new view, and selecting
					Futures Building. If the features, projectors, and timeline events are set up correctly, this trajectory will show a reasonable time series with each of the features changing as
					expected.</p></li>
		</ol>

		<div class="text-right"><a href="#top" class="nav-link text-secondary font-italic">Back to top</a></div>

		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Analyzing the Scenario"
			bodyId="analyzing"
			initiallyExpanded="true"
			headerCssClasses="h3 mb-3 text-primary"
			bodyCssClasses="bg-light rounded p-3 mb-3">

		<p>Once you have created a project with features, timeline events, and members, you are ready to start analyzing the scenario. There are several structured analytic techniques (SATs) that
			Scenario Explorer provides to support anticipatory thinking. Depending on your goals, you may use one or all of the techniques.</p>

		<p>Scenario Explorer supports the following techniques:</p>
		<ol>
			<li><strong>Futures Building</strong>
				<p>This technique starts with a tree containing only the root Now node. The analyst builds a set of scenarios by sequentially adding conditioning events that logically fit together as
					a form of narrative (albeit in tree form). While the analyst may be focused on expressing a single scenario (i.e., a single trajectory through a tree of possible futures) that is
					relevant to them, the system is automatically populates the tree with all possible compositions of the events elicited. Futures Building is useful when the analyst has a set of
					conditioning events that they wish to enter into the system that share common narrative thread.</p></li>
			<li><strong>Smart Query</strong>
				<p>Smart Queries allow one ask questions about a large number of trajectories in the tree in an intuitive manner. A Smart Query should be used when the analyst wishes to determine
					which conditioning events play a critical role in differentiating possible futures with respect to a specific set of features. It allows the analyst to gain new insight either by
					seeing how conditioning events and outcomes (regardless of their source) influence futures.</p></li>
			<li><strong>Extreme States (coming soon)</strong>
				<p>Extreme States is a technique derived from the concept of a Pre-mortem analysis. Rather than starting with a blank slate and working forward, Extreme States starts with a goal State
					and attempts to elicit conditioning events that bring trajectories closer to this goal state. This type of backcasting is intended to aid the analyst in shifting their perspective
					to imagining how the situation might have gotten from Now to the Extreme State.</p></li>
			<li><strong>What If (coming soon)</strong>
				<p>A What If Analysis should be used when an analyst has questions about specific features and how sensitive things are to those values. For example, they can change the Now state and
					see the effects ripple through the tree.</p></li>
		</ol>

		<h4 class="mt-5 mb-3 text-primary" id="topic_futures_building">Futures Building - Self-directed Elicitation of Conditioning Events</h4>
		<p>The primary goal of Futures Building is to elicit interesting and feasible Conditioning Events from the analyst and display them. Futures Building accomplishes this by enabling an analyst
			to express a scenario that they believe to be relevant to the project. This technique starts with a tree containing only the root Now node. The analyst builds a set of scenarios by
			sequentially adding conditioning events that logically fit together as a form of narrative (albeit in tree form). While the analyst may be focused on expressing a single scenario (i.e., a
			single trajectory through a tree of possible futures) that is relevant to them, the system is automatically populates the tree with all possible compositions of the events elicited.</p>
		<h5 class="text-primary">When to Use It</h5>
		<p>Futures Building is useful when the analyst has a set of conditioning events that they wish to enter into the system that share common narrative thread. Ideally, each view instance would
			stand on its own as a short story of what might happen in the future. This is the default elicitation view for the Scenario Explorer platform.</p>
		<h5 class="text-primary">Value Added</h5>
		<p>When analysts express their knowledge as conditioning events in the Futures Building view, they are sharing their knowledge with the team and the system using a common model of the
			project-space. A Futures Building session can be performed alone or as a team. When/if the entered conditioning events appear on other views (which may happen automatically) they can be
			traced back to their origin view - a Futures Building view provide a way to group related conditioning events to more effectively explain their meaning by offering context.</p>
		<h5 class="text-primary">The Method</h5>
		<p>The Scenario Explorer System treats each instance of Futures Building as a separate View. So, users can collaborate on a shared Futures Building scenario or refer back to it to understand
			the intention of a conditioning event that might come up in another context within the system. Behind the scenes, the conditioning events being elicited are automatically being applied at
			the Project level. So, instead of one large Futures Building View, we recommend that users create many smaller/simpler Views around specific domain-relevant narrative topics.</p>
		<ol>
			<li>
				<strong>Creating a New Futures Building View</strong>
				<p>From the Project dashboard, users can add a new Futures Building view. The view will appear with only the Now state for initial context.</p>
			</li>
			<li>
				<strong>Adding New Conditioning Events</strong>
				<p>Futures Building is an iterative process of adding conditioning events in the time-sequence that we expect them to occur. These explicitly capture potential events that change the
					context of the world we are modeling, and implicitly capture cascading effects. Users can press the &quot;Add Conditioning Event&quot; button on the right side panel under
					Conditioning
					Events. Existing conditioning events, created in other views, can also be assigned to this view.</p>
			</li>
			<li>
				<strong>Integrating the Conditioning Event into the Tree</strong>
				<p>Newly created Conditioning Events are automatically added to the Tree and integrated with the existing conditioning events and states. By default a conditioning event will only take
					place once in a trajectory. However, the same conditioning event can be applied in multiple trajectories after the point at which the trajectories diverge. At this point the user
					can see the application of their prerequisites and outcome/effects and determine if edits are required to the conditioning event.</p>
			</li>
		</ol>

		<h4 class="mt-5 mb-3 text-primary" id="topic_smart_query">Smart Query - Discovering the Dependencies of Clusters of Future States</h4>
		<p>Smart Query is a technique for extracting knowledge from the Scenario Explorer System rather than eliciting knowledge from analysts to put into the system. Smart Queries allow one ask
			questions about a large number of trajectories in the tree in an intuitive manner. The goal is provide results as a simplified tree that clusters leaf states together (based on similarity
			to query features) and determine which conditioning events best discriminate the clusters.</p>
		<p>For example, if we perform a Smart Query on a feature that represents the average price of electric cars, all futures could cluster into ranges of the cars being generally expensive,
			mid-range, or cheap. We might find that conditioning events and outcomes such as &quot;major tax incentives given&quot; would exist on the trajectories for mid-range and cheap, but not
			expensive.</p>
		<h5 class="text-primary">When to Use It</h5>
		<p>A Smart Query should be used when the analyst wishes to determine which conditioning events play a critical role in differentiating possible futures with respect to a specific set of
			features. It allows the analyst to gain new insight either by seeing how conditioning events and outcomes (regardless of their source) influence futures.</p>
		<h5 class="text-primary">Value Added</h5>
		<p>Smart Queries extract short stories from large data sets by finding the most salient conditioning events and outcomes for understanding how a feature might evolve. The critical
			conditioning events and outcomes can be used as leading indicators to determine which cluster an evolving scenario is likely to be in.</p>
		<h5 class="text-primary">The Method</h5>
		<p>The Scenario Explorer System treats each instance of Smart Queries as a separate view, since they can be computationally expensive to produce; the query results are stored and updated on
			demand.</p>
		<ol>
			<li>
				<strong>Creating a Smart Query View</strong>
				<p>From the Project dashboard, users can add a Smart Query view.</p>
			</li>
			<li>
				<strong>Specifying the Query Features</strong>
				<p>The user is presented with a list of all of the features in the project-space. They simply select the features on which they wish to cluster. One feature makes clustering straight
					forward, selecting too many features can result in more complex clusterings.</p>
			</li>
			<li>
				<strong>Execute the Query</strong>
				<p>Once the query features are identified, the query can be executed. The Scenario Explorer System will automatically cluster the futures states into some number of clusters
					representing the groupings of values for the query features. Clusters are represented as Aggregated States on the left side of the tree, selecting it will display the details of
					that cluster.</p>
				<p>The tree structure leading up to the aggregated states will be composed of the critical conditioning events and their outcomes. The system organizes the conditioning events/outcomes
					by how they distinguish between states.</p>
			</li>
		</ol>

		<h4 class="mt-5 mb-3 text-primary" id="topic_extreme_states">Extreme States - Goal-directed Elicitation of Conditioning Events</h4>
		<p>The Extreme States technique derives from the concept of a Pre-mortem analysis. Rather than starting with a blank slate and working forward, Extreme States starts with a goal State and
			attempts to elicit conditioning events that bring trajectories closer to this goal state. This is intended to aid the analyst in shifting their perspective to imagining how the situation
			might have gotten from Now to the Extreme State.</p>
		<h5 class="text-primary">When to Use It</h5>
		<p>It is sometimes difficult to diverge from the events we expect. Extreme States provides a method of shifting ones perspective from looking forward at a wide open expanse of possibilities to
			looking back and explaining how the situation got to that Extreme State. The trajectory elicited is not the primary objective since it likely highly unlikely, however the individual
			feasible conditioning events that discovered to lead to the Extreme State can also be used to enrich scenarios and introduce trajectories that are less extreme and more likely.</p>
		<h5 class="text-primary">Value Added</h5>
		<p>It is sometimes difficult to diverge from the events we expect. Extreme States provides a method of shifting ones perspective from looking forward at a wide open expanse of possibilities to
			looking back and explaining how the situation got to that Extreme State. The trajectory elicited is not the primary objective since it likely highly unlikely, however the individual
			feasible conditioning events that discovered to lead to the Extreme State can also be used to enrich scenarios and introduce trajectories that are less extreme and more likely.</p>
		<h5 class="text-primary">The Method</h5>
		<p>The Scenario Explorer System treats each instance of Extreme States as a separate View. An Extreme States view will likely be relatively small, since once a trajectory between Now and the
			Extreme State is found, the process is complete.</p>
		<ol>
			<li>
				<strong>Creating an Extreme State View</strong>
				<p>From the Project dashboard, users can add a new view and select &quot;Extreme State.&quot;</p>
			</li>
			<li>
				<strong>Specifying the Extreme State</strong>
				<p>An extreme state is defined by setting goal values for some subset of the features in the project. You are not required to set all the values, since such a state may prove
					impossible to reach. Any unspecified features are considered wildcards that are satisfied by any value.</p>
			</li>
			<li>
				<strong>Checking for an Existing Trajectory</strong>
				<p>The Scenario Explorer System will automatically search the existing conditioning events to determine if a combination exists that meets the specified extreme state. If there is, it
					will be shown to the user and we are done.</p>
			</li>
			<li>
				<strong>Adding Conditioning Events</strong>
				<p>The analyst(s) will add new feasible conditioning events to the view. Each new conditioning event will be added to the tree and the states will be colored by how close they are to
					the goal extreme state. Selecting a state will color the relevant features by how close they are to the goal value. This is intended to help focus the analyst in identifying new
					conditioning events by their effect.</p>
			</li>
		</ol>

		<h4 class="mt-5 mb-3 text-primary" id="topic_what_if">What If - Discovering the Sensitivities of Scenarios</h4>
		<p>What If Analysis is used to explore the sensitivities of the scenarios represented in the tree. A What If Analysis starts with an existing view and allows the analyst to create an overlay
			on the project-space in which they can modify specific feature values and conditioning event outcome effects at will and without changing other views.</p>
		<h5 class="text-primary">When to Use It</h5>
		<p>A What If Analysis should be used when an analyst has questions about specific features and how sensitive things are to those values. For example, they can change the Now state and see the
			effects ripple through the tree.</p>
		<h5 class="text-primary">Value Added</h5>
		<p>A What If Analysis allows an analyst to explore nuances of a specific feature or view of the project-space. These overlays do not modify or feed back into the project data. They are
			intended to be temporary.</p>
		<h5 class="text-primary">The Method</h5>
		<p>The Scenario Explorer System allows What If Analysis Views to be initiated from any other view.</p>
		<ol>
			<li>
				<strong>Creating a What If Analysis Overlay</strong>
				<p>From an existing view, select &quot;Create a What If Analysis Overlay.&quot;</p>
			</li>
			<li>
				<strong>Editing the values of features in any State</strong>
				<p>Typically, the value of features in a state are presented as Read-Only because they are generated by Projectors and/or the Effects of Conditioning Event Outcomes. In a What If
					Analysis Overlay, all feature values can be edited in the right side panel when a State is selected. Save the changes to the Overlay when completed.</p>
			</li>
			<li>
				<strong>Updating the Tree</strong>
				<p>The edit state in the tree will change and all values to the right in the tree will be updated to reflect the change. Changed nodes will be colored to identify their degree of
					change from their previous values.</p>
			</li>
		</ol>

		<div class="text-right"><a href="#top" class="nav-link text-secondary font-italic">Back to top</a></div>

		</imaginationsupport:collapsiblesection>

	</imaginationsupport:maincontentsection>
</imaginationsupport:maincontent>
</body>
</html>
