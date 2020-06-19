import datetime
import json
import requests

############################################################################################################################################

READ_ONLY = False

VERBOSE = False

DELETE_PROJECT_AT_END = True

##################################################
# source server

# SOURCE_URI = 'https://dev.imaginationsupport.com/scenarioexplorer'
# SOURCE_USERNAME = ''
# SOURCE_PASSWORD = ''
# SOURCE_PROJECT_ID = ''

SOURCE_URI = 'https://dev.imaginationsupport.com/scenarioexplorer'
SOURCE_USERNAME = ''
SOURCE_PASSWORD = ''
SOURCE_PROJECT_ID = ''

##################################################
# destination

# DESTINATION_URI = 'https://dev.imaginationsupport.com/scenarioexplorer'
# DESTINATION_USERNAME = ''
# DESTINATION_PASSWORD = ''

DESTINATION_URI = 'https://demo.imaginationsupport.com/scenarioexplorer'
DESTINATION_USERNAME = ''
DESTINATION_PASSWORD = ''

# DESTINATION_URI = 'http://las-vm-dev1:8080/scenarioexplorer'
# DESTINATION_USERNAME = ''
# DESTINATION_PASSWORD = ''


############################################################################################################################################

def main():
    if READ_ONLY:
        print('####################')
        print('##                ##')
        print('## READ ONLY MODE ##')
        print('##                ##')
        print('####################\n')

    ids_to_convert = {}

    # utc_now = datetime.datetime.utcnow()
    local_time = datetime.datetime.now()

    ##################################################

    source_session = requests.Session()

    # login to source
    if SOURCE_USERNAME and SOURCE_PASSWORD:
        print('Login to source...')
        run_api_login(source_session, SOURCE_URI, SOURCE_USERNAME, SOURCE_PASSWORD)
        print()

    # get source project
    print('Download source project...')
    source_project = run_api_call_get(source_session, '%s/api/project/%s' % (SOURCE_URI, SOURCE_PROJECT_ID), 'project')
    if VERBOSE:
        print(json.dumps(source_project, indent=4, sort_keys=True))
    print()

    # get source timeline events
    print('Download source timeline events...')
    source_timeline_events = run_api_call_get(source_session, '%s/api/project/%s/timelineEvents' % (SOURCE_URI, SOURCE_PROJECT_ID), 'timelineEvents')
    if VERBOSE:
        print(json.dumps(source_timeline_events, indent=4, sort_keys=True))
    print()

    # get features
    print('Download source features...')
    source_features = run_api_call_get(source_session, '%s/api/project/%s/features' % (SOURCE_URI, SOURCE_PROJECT_ID), 'features')
    if VERBOSE:
        print(json.dumps(source_features, indent=4, sort_keys=True))
    print()

    # get source views
    print('Download source views...')
    source_views = run_api_call_get(source_session, '%s/api/project/%s/views' % (SOURCE_URI, SOURCE_PROJECT_ID), 'views')
    if VERBOSE:
        print(json.dumps(source_views, indent=4, sort_keys=True))
    print()

    # get source conditioning events
    print('Download source conditioning events...')
    source_conditioning_events = run_api_call_get(source_session, '%s/api/project/%s/conditioningEvents' % (SOURCE_URI, SOURCE_PROJECT_ID), 'conditioningEvents')
    if VERBOSE:
        print(json.dumps(source_conditioning_events, indent=4, sort_keys=True))
    print()

    ##################################################

    destination_session = requests.Session()

    # login to source
    if DESTINATION_USERNAME and DESTINATION_PASSWORD:
        print('Login to destination...')
        run_api_login(destination_session, DESTINATION_URI, DESTINATION_USERNAME, DESTINATION_PASSWORD)
        print()

    # put the new project
    print('Uploading new project...')
    project_to_create = prepare_item_to_insert(source_project, ids_to_convert)
    project_to_create.pop('owner')  # remove the owner so it is set to the logged in user
    project_to_create['name'] = '%s (imported %s)' % (project_to_create['name'], local_time.strftime("%Y-%m-%d %H:%M:%S"))
    destination_project = run_api_call_put(destination_session, '%s/api/project' % DESTINATION_URI, {'project': project_to_create}, 'project')
    ids_to_convert[source_project['id']] = destination_project['id']
    print()

    # run_api_call_post(
    #     destination_session,
    #     '%s/api/project/%s' % (DESTINATION_URI, destination_project['id']),
    #     {'project': destination_project, 'projectId': destination_project['id']}, 'project')

    # put the new timeline events
    print('Uploading new timeline events...')
    for source_timeline_event in source_timeline_events:
        destination_timeline_event_to_create = prepare_item_to_insert(source_timeline_event, ids_to_convert)
        destination_timeline_event = run_api_call_put(
            destination_session,
            '%s/api/project/%s/timelineEvent' % (DESTINATION_URI, destination_project['id']),
            {'timelineEvent': destination_timeline_event_to_create},
            'timelineEvent')
        if VERBOSE:
            print(destination_timeline_event)
        ids_to_convert[source_timeline_event['id']] = destination_timeline_event['id']
    if len(source_timeline_events) == 0:
        print('(none)')
    print()

    # put new features
    print('Uploading new features...')
    for source_feature in source_features:
        destination_feature_to_create = prepare_item_to_insert(source_feature, ids_to_convert)
        destination_feature = run_api_call_put(
            destination_session,
            '%s/api/project/%s/feature' % (DESTINATION_URI, destination_project['id']),
            {'feature': destination_feature_to_create},
            'feature')
        if VERBOSE:
            print(destination_feature)
        ids_to_convert[source_feature['id']] = destination_feature['id']
    if len(source_features) == 0:
        print('(none)')
    print()

    # put the new views
    print('Uploading new views...')
    for source_view in source_views:
        destination_view_to_create = prepare_item_to_insert(source_view, ids_to_convert)
        destination_view = run_api_call_put(
            destination_session,
            '%s/api/project/%s/view' % (DESTINATION_URI, destination_project['id']),
            {'view': destination_view_to_create},
            'view')
        if VERBOSE:
            print(destination_view)
        ids_to_convert[source_view['id']] = destination_view['id']
    if len(source_views) == 0:
        print('(none)')
    print()

    # put the new conditioning events
    print('Uploading new conditioning events...')
    for source_conditioning_event in source_conditioning_events:
        destination_conditioning_event_to_create = prepare_item_to_insert(source_conditioning_event, ids_to_convert)
        destination_conditioning_event = run_api_call_put(
            destination_session,
            '%s/api/project/%s/conditioningEvent' % (DESTINATION_URI, destination_project['id']),
            {'conditioningEvent': destination_conditioning_event_to_create},
            'conditioningEvent')
        if VERBOSE:
            print(destination_conditioning_event)
        ids_to_convert[source_conditioning_event['id']] = destination_conditioning_event['id']
    if len(source_conditioning_events) == 0:
        print('(none)')
    print()

    if DELETE_PROJECT_AT_END:
        print('Cleaning up...')
        run_api_call_delete(destination_session, '%s/api/project/%s' % (DESTINATION_URI, destination_project['id']))
        print()

    return


############################################################################################################################################

def run_api_login(session, server_base_uri, username, password):
    login_form_uri = '%s/' % server_base_uri
    print('  GET:      %s' % login_form_uri)
    response = session.get(login_form_uri)
    if VERBOSE:
        print('    HTTP status:   %d' % response.status_code)
        print('    response size: %d' % len(response.content))

    login_action = '%s/j_security_check' % server_base_uri
    print('  POST:     %s' % login_action)
    response = session.post(login_action, data={'j_username': username, 'j_password': password})
    if VERBOSE:
        print('    HTTP status:   %d' % response.status_code)
        print('    response size: %d' % len(response.content))


############################################################################################################################################

def run_api_call_get(session, uri, key):
    print('  GET:      %s' % uri)
    response = session.get(uri)
    if VERBOSE:
        print('    HTTP status:   %d' % response.status_code)
        print('    response size: %d' % len(response.content))
    json_response = json.loads(response.content)
    if 'success' not in json_response or not json_response['success']:
        handle_error_response(json_response)
    if key not in json_response:
        raise Exception('Could not find "%s" in: %s' % (key, json_response))
    return json_response[key]


############################################################################################################################################


def run_api_call_put(session, uri, data, key):
    if READ_ONLY:
        print('(READ ONLY)')
        return json.loads('{"id":"0"}')
    else:
        print('  PUT:      %s' % uri)
        if VERBOSE:
            print('            %s' % json.dumps(data))
        response = session.put(uri, data=json.dumps(data))
        if VERBOSE:
            print('    HTTP status:   %d' % response.status_code)
            print('    response size: %d' % len(response.content))
        json_response = json.loads(response.content)
        if 'success' not in json_response or not json_response['success']:
            handle_error_response(json_response)
        if key not in json_response:
            raise Exception('Could not find "%s" in: %s' % (key, json_response))
        return json_response[key]


############################################################################################################################################

def run_api_call_post(session, uri, data, key):
    if READ_ONLY:
        print('(READ ONLY)')
        return json.loads('{"id":"0"}')
    else:
        print('  POST:     %s' % uri)
        # print('            %s' % json.dumps(data))
        response = session.post(uri, data=json.dumps(data))
        if VERBOSE:
            print('    HTTP status:   %d' % response.status_code)
            print('    response size: %d' % len(response.content))
        json_response = json.loads(response.content)
        if 'success' not in json_response or not json_response['success']:
            handle_error_response(json_response)
        if key not in json_response:
            raise Exception('Could not find "%s" in: %s' % (key, json_response))
        return json_response[key]


############################################################################################################################################


def run_api_call_delete(session, uri):
    if READ_ONLY:
        print('(READ ONLY)')
        return json.loads('{"id":"0"}')
    else:
        print('  DELETE:   %s' % uri)
        response = session.delete(uri)
        if VERBOSE:
            print('    HTTP status:   %d' % response.status_code)
            print('    response size: %d' % len(response.content))


############################################################################################################################################

def handle_error_response(json_response):
    print()
    if 'request' in json_response:
        print('Request:')
        print(json.dumps(json_response['request'], indent=4, sort_keys=True))
    if 'errorMessage' in json_response:
        print('Error Message: %s' % json_response['errorMessage'])
    if 'stackTrace' in json_response:
        print('Stack Trace:   %s' % json_response['stackTrace'])
    exit(1)
    # raise Exception('Error: %s' % json_response)


############################################################################################################################################


def prepare_item_to_insert(source_item, ids_to_convert):
    working_json = json.dumps(source_item)

    for key, value in ids_to_convert.items():
        # print('    %s=%s' % (key, value))
        working_json = working_json.replace(key, value)

    updated_item = json.loads(working_json)

    if 'id' in updated_item:
        updated_item.pop('id')

    return updated_item


############################################################################################################################################

if __name__ == "__main__":
    main()

############################################################################################################################################
