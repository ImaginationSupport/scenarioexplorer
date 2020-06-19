<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head minimalJS="true" />
<script type="text/javascript">
	$( function()
	{
		$( '.nav-link' ).on( 'click', function()
		{
			const divId = $( this ).attr( 'href' );
			$( 'html, body' ).animate( { 'scrollTop' : $( divId ).offset().top - 64 }, 100 );
			return;
		} );
	} );

	function toggleExample( i )
	{
		const bodyDiv = $( '#example-body-' + i ).toggle();

		$( '#example-header-' + i ).text( bodyDiv.is( ":visible" ) ? 'Hide Example' : 'Example' );

		return;
	}
</script>
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:maincontent title="Scenario Explorer Glossary">
	<imaginationsupport:maincontentsection>

		<h2 class="mb-3 text-primary" id="top">Scenario Explorer Glossary</h2>

		<table class="table table-striped table-bordered table-hover">
			<tbody>

			<tr><td class="font-bold">Conditional  Forecasting</td>
				<td>Conditional forecasts are predictions of the future assuming some given condition is true.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-00" onclick="toggleExample('00')">Example</div>
						<div id="example-body-00" style="display: none">A general forecast question might be &quot;what will the average price of an electric car be in 2030?&quot; while a conditional forecast might be &quot;what will the average price of an electric car be in 2030, assuming that Tesla releases the Model 3 as advertised?&quot; The additional qualifier limits the scope of the forecast question because we no longer have to consider alternatives possibilities for the Tesla Model 3 release.</div></div>
				</td></tr>

			<tr><td class="font-bold">Conditioning Event</td>
				<td>A conditioning event (CE) is input by the user to introduce branching to the tree of futures.  Scenario Explorer then places the CE in all available locations on tree and as a result, the context changes for every node after it in any trajectory.  Analysts then can see how features change as an effect of the CE placement.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-01" onclick="toggleExample('01')">Example</div>
						<div id="example-body-01" style="display: none">An example of a conditioning event would be "2016 Election in US" as the outcome would have effects on certain features, such as President (Trump, Clinton, Stein), # Republican Senators, and Probability of ACA being repealed.</div></div>
				</td></tr>

			<tr><td class="font-bold">Conditioning Event: Effect</td>
				<td>An effect is a change to the features between the state preceding a conditioning event and the state immediately following a conditioning event. Each outcome can have its own set of effects.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-02" onclick="toggleExample('02')">Example</div>
						<div id="example-body-02" style="display: none">A conditioning event like "Vote to Sanction country X" could have an outcome of "Light sanctions imposed" that would have an effect such as setting the feature of "tariff on exports from country X" to 40% (new value)</div></div>
				</td></tr>

			<tr><td class="font-bold">Conditioning Event: Outcome</td>
				<td>Every conditioning event has multiple potential outcomes, each outcome has some impact on some set of features. There may be multiple outcomes, depending on how the feature type interacts with the conditioning event.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-03" onclick="toggleExample('03')">Example</div>
						<div id="example-body-03" style="display: none">The election (conditioning event)'s outcome would be the selection of the president (feature).</div></div>
				</td></tr>

			<tr><td class="font-bold">Conditioning Event: Precondition</td>
				<td>In order for a conditioning event to happen, there may be certain conditions that exist. A precondition is a way constrain the possible locations for a conditioning event to occur on the tree.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-04" onclick="toggleExample('04')">Example</div>
						<div id="example-body-04" style="display: none">For example, an oil crash (conditioning event) could only happen when oil price (feature) was below $20 per barrel (precondition).</div></div>
				</td></tr>

			<tr><td class="font-bold">Context</td>
				<td>Context is the full narrative of both the historical sequence of events and changes to features in the project (ex. historic data, expertise expectations).
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-05" onclick="toggleExample('05')">Example</div>
						<div id="example-body-05" style="display: none">A trajectory through the tree that shows the logical sequence of a future - such as an election was won by the Socialist party and the world population is 7 billion.</div></div>
				</td></tr>

			<tr><td class="font-bold">Domain</td>
				<td>A domain is a specific area of expertise; the domain relates to what is important and knowable within that area.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-06" onclick="toggleExample('06')">Example</div>
						<div id="example-body-06" style="display: none">The electric vehicles (EV) domain would include research on the price of electricity, consumer attitudes towards EV, number of charging stations in the US, mileage per battery charge, fuel price, etc.</div></div>
				</td></tr>

			<tr><td class="font-bold">Entities</td>
				<td>Some features are global, but others are contained with a logical entity, such as an organization, agent, or structure. Entities map features so each entity of a type will have their own version of that feature.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-07" onclick="toggleExample('07')">Example</div>
						<div id="example-body-07" style="display: none">"University" might be an entity described by #students, #professors, and $tuition.  In that way one could analyze the organizations of UNC, NCSU, and Duke as aggregates of chosen features.</div></div>
				</td></tr>

			<tr><td class="font-bold">Feature</td>
				<td>Features are the key variables that analysts track as they interact with possible futures. Features have values that change over time and in response to the context--similar in concept to algebraic variables or logical fluents.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-08" onclick="toggleExample('08')">Example</div>
						<div id="example-body-08" style="display: none">The cost of a computer, the state of a road, or the popularity of an idea - all of these change over time and can be greatly impacted by the context.</div></div>
				</td></tr>

			<tr><td class="font-bold">Feature Type: Boolean </td>
				<td>A feature type that has only two possible values (true/false).
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-09" onclick="toggleExample('09')">Example</div>
						<div id="example-body-09" style="display: none">Boolean features might denote polarities such as yes/no; Democrat/Republican; or high/low.</div></div>
				</td></tr>

			<tr><td class="font-bold">Feature Type: Integer</td>
				<td>A feature type useful for whole number quantities.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-10" onclick="toggleExample('10')">Example</div>
						<div id="example-body-10" style="display: none">Integer features can track products sold or populations.  Whole numbers ensure that the analysis does not report half a person or half of a product as an outcome.</div></div>
				</td></tr>

			<tr><td class="font-bold">Feature Type: Multiple Choice</td>
				<td>A feature type offering a list of values (A, B, C, D).
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-11" onclick="toggleExample('11')">Example</div>
						<div id="example-body-11" style="display: none">Multiple choice feature might include multiple names (such as Trump, Clinton, and Stein, or yes, maybe, no, or even Likert-type values).</div></div>
				</td></tr>

			<tr><td class="font-bold">Feature Type: Probability</td>
				<td>A feature type that describes a likelihood, assigned by the user as a number between 0 and 1 (0=impossibility and 1=certainty).
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-12" onclick="toggleExample('12')">Example</div>
						<div id="example-body-12" style="display: none">In a flip of a coin, there is a 50% likelihood, or a .5 probability that the coin will land on heads. There is the same probability that it will land on tails. There is a 0% chance that a coin will not land (unless you are in space).</div></div>
				</td></tr>

			<tr><td class="font-bold">Feature Type: Text</td>
				<td>A feature type which allows the user to input text.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-13" onclick="toggleExample('13')">Example</div>
						<div id="example-body-13" style="display: none">An analyst may want to include a qualitative assessment or undefined feature that does not fit easily in multiple choice or Boolean types, such as a sentence or paragraph.</div></div>
				</td></tr>

			<tr><td class="font-bold">Now State/Origin State/Seed/Root</td>
				<td>The user can select any date to start building the chain of events: it is the root of the tree, the starting point for looking into the future
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-14" onclick="toggleExample('14')">Example</div>
						<div id="example-body-14" style="display: none">The start date of a project is the original &quot;now state.&quot;</div></div>
				</td></tr>

			<tr><td class="font-bold">Outcomes</td>
				<td>Every conditioning event has multiple potential outcomes, each outcome has some impact on some set of features.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-15" onclick="toggleExample('15')">Example</div>
						<div id="example-body-15" style="display: none">An outcome of the election (conditioning event) would be the selection of the president (feature).  There may be multiple outcomes, depending on how the feature type interacts with the conditioning event. "Outcome Label" is useful for helping collaborators understand a more generalizable narrative for the state that holds the effects of the conditioning event. In this case, one might label the outcome "Election Outcome"</div></div>
				</td></tr>

			<tr><td class="font-bold">Project</td>
				<td>The project is the specific project or research question that the analysts are using the platform to analyze.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-16" onclick="toggleExample('16')">Example</div>
						<div id="example-body-16" style="display: none">A project using Future Builder might be: What does electrical vehicle adoption look like from 2007-2040 (Electric Vehicle Adoption)? Or How will North Korean nuclear testing affect economic stability in Japan (North Korea's impact on Japan)?</div></div>
				</td></tr>

			<tr><td class="font-bold">Project Space</td>
				<td>The project space is composition of all of the data that users have contributed to a project. It expresses the full range of the knowledge in the system about that particular project (includes features, entities, conditioning events, historical data, etc).
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-17" onclick="toggleExample('17')">Example</div>
						<div id="example-body-17" style="display: none">All of the information put in the system about electric vehicles in order to create different views (What if, Future Build, Smart Query and Extreme States).</div></div>
				</td></tr>

			<tr><td class="font-bold">Projector</td>
				<td>A projector is predictive model that forecasts future values of a feature.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-18" onclick="toggleExample('18')">Example</div>
						<div id="example-body-18" style="display: none">A compound rate projector will tell the value based on an amortization schedule.</div></div>
				</td></tr>

			<tr><td class="font-bold">State</td>
				<td>A state is a generated point which captures all of the values of that instance in the project space. Since a value can be ascribed for each feature of each entity, a state is the convergence of any point in time within any context of the project space.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-19" onclick="toggleExample('19')">Example</div>
						<div id="example-body-19" style="display: none">A state would be a computer-generated location on the tree for electric vehicle adoption where features such as oil price is X, the president is Trump, and the public opinion about EV is positive.</div></div>
				</td></tr>

			<tr><td class="font-bold">Timeline Events</td>
				<td>A linear time series of contextual situations that are not dependent on the decisions/actions/events of the project.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-20" onclick="toggleExample('20')">Example</div>
						<div id="example-body-20" style="display: none">Timeline events include seasons, elections, planned initiatives, or other known events.</div></div>
				</td></tr>

			<tr><td class="font-bold">Tree</td>
				<td>The tree is a visual representation of a set of possible futures and how they diverge from the now or root state.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-21" onclick="toggleExample('21')">Example</div>
						<div id="example-body-21" style="display: none">The tree in Futures Building view includes only the conditioning events that the user entered into that view (i.e., have an origin of that view) or have attached from the project (i.e., origin is elsewhere but it is shared into this view).</div></div>
				</td></tr>

			<tr><td class="font-bold">View</td>
				<td>The view is a graphical user interface (GUI) that provides a specific perspective on the project space. The GUI dynamically updates as the underlying parameters of the project space change based on the user.  View configuration of the interface (such as the query parameters used) and the design function (e.g. graphically show query results) control the perspective of view.</td></tr>

			<tr><td class="font-bold">View Type: Futures Building</td>
				<td>Systematically elicits and forward chains causal reasoning for systematically alternative futures analysis. The goal, then, is to analyze and understand the conditioning events that lead to each future.
					<div class="mt-2">
						<div class="font-weight-bold clickable text-primary" id="example-header-22" onclick="toggleExample('22')">Example</div>
						<div id="example-body-22" style="display: none">The purpose of Futures Building is to enable a team of analysts to select a limited scope of topics and systematically think through potential conditioning events using forward chaining.</div></div>
				</td></tr>

			<tr><td class="font-bold">Project Name</td>
				<td>The project name identifies the specific scenario or research question that you and your team will be analyzing. </td></tr>

			<tr><td class="font-bold">Project Description</td>
				<td>A description of the project is useful for providing mission-specific context to collaborating team members. </td></tr>

			<tr><td class="font-bold">Project Start Date</td>
				<td>The start date creates the root node of the tree, also known as the now state. </td></tr>

			<tr><td class="font-bold">Project End Date</td>
				<td>The end date maps the scenario to specific length of time, for example, 5 months or 5 years from the start date. </td></tr>

			<tr><td class="font-bold">Project Increment</td>
				<td>The increment provides a time resolution for the Scenario Explorer to anticipate changes (days, months, or years). </td></tr>

			<tr><td class="font-bold">Import Project</td>
				<td>If you would like to import a project you previously exported, you can use the file selection or simply drag/drop the file onto the page.</td></tr>

			<tr><td class="font-bold">Timeline Name</td>
				<td>Provide a short, clear name for the timeline event to provide context for your scenario.</td></tr>

			<tr><td class="font-bold">Timeline Description</td>
				<td>While optional, a description will help collaborators understand the timeline event's role in the project. </td></tr>

			<tr><td class="font-bold">Timeline Start Date</td>
				<td>Identify when the event begins. </td></tr>

			<tr><td class="font-bold">Timeline End Date</td>
				<td>Identify when the event ends. </td></tr>

			<tr><td class="font-bold">Timeline URL</td>
				<td>Provide a URL with additional information about this event (optional). Collaborators will be able to access the URL. </td></tr>

			<tr><td class="font-bold">CE Name</td>
				<td>Provide a short, clear name that identifies the role of the conditioning event. </td></tr>

			<tr><td class="font-bold">CE Description</td>
				<td>While optional, a description will help collaborators understand and remember the conditioning event's role in the project.</td></tr>

			<tr><td class="font-bold">CE Outcomes</td>
				<td>Every conditioning event has multiple potential outcomes, each outcome has some impact on some set of features. </td></tr>

			<tr><td class="font-bold">Project Access</td>
				<td>Scenario Explorer allows for remote collaboration on challenging projects.  Project access allows the owner to decide who can collaborate or change aspects of the project.</td></tr>

			<tr><td class="font-bold">Project Access Member</td>
				<td>Members can edit all features, timeline events, and create new views, but do not have authority to delete the project. </td></tr>

			<tr><td class="font-bold">Project Access Owner</td>
				<td>The Owner has full editorial control over the project, including adding and deleting members. </td></tr>

			<tr><td class="font-bold">Feature Name</td>
				<td>Provide a feature name that clearly describes the variable, preferably a name that is independently recognizable from the specific project. </td></tr>

			<tr><td class="font-bold">Feature Description</td>
				<td>While optional, a description of the feature will help collaborators understand its role in the project. </td></tr>

			<tr><td class="font-bold">Feature Type</td>
				<td>Currently, Scenario Explorer supports Boolean, multiple choice, integer, decimal, probability, and text feature types. </td></tr>

			<tr><td class="font-bold">Feature Projector</td>
				<td>A projector is predictive model that forecasts future values of a feature. Example: A compound rate projector will tell the value based on an amortization schedule. </td></tr>

			<tr><td class="font-bold">Feature Initial Value</td>
				<td>Assigns the feature value at the starting point (now state) of the project.</td></tr>

			<tr><td class="font-bold">Feature Min</td>
				<td>If applicable, assign the minimum value allowed for the feature (optional).</td></tr>

			<tr><td class="font-bold">Feature Max</td>
				<td>If applicable, assign the maximum value allowed for the feature (optional).</td></tr>

			<tr><td class="font-bold">Feature Decimal Places</td>
				<td>Select the number of decimal places to show.</td></tr>

			<tr><td class="font-bold">Feature Add New Multiple Choice</td>
				<td>The text of the choice to add</td></tr>

			<tr><td class="font-bold">Feature Existing Multiple Choice</td>
				<td>The existing multiple choice entries</td></tr>

			<tr><td class="font-bold">Outcome Likelihood</td>
				<td>You may assign different likelihood probabilities to each outcome of a conditioning event.</td></tr>

			<tr><td class="font-bold">Scenario</td>
				<td>A scenario is all of the components (conditioning events, states, timeline events, features, projectors, etc.)  that comprise a specific trajectory through the tree. </td></tr>

			</tbody>
		</table>


	</imaginationsupport:maincontentsection>
</imaginationsupport:maincontent>
</body>
</html>
