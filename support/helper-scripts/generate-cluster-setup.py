#!/usr/bin/env python3

########################################################################################################################
#
# NOTE:  The should be run from the "console-tools/working-dir" folder
#
########################################################################################################################

import os
import random
from pathlib import Path


########################################################################################################################

class ClusterSetup:
    def __init__(self,
                 url_prefix,
                 docker_repo_name_suffix,
                 cluster_uri_name,
                 release_tier_tag,
                 ldap_password,
                 ldap_port,
                 mongo_password,
                 mongo_port,
                 max_web_ui_ram,
                 max_mongo_ram,
                 include_swagger_container):
        self.url_prefix = url_prefix
        self.docker_repo_name_suffix = docker_repo_name_suffix
        self.cluster_uri_name = cluster_uri_name
        self.release_tier_tag = release_tier_tag
        self.ldap_password = generate_password() if ldap_password is None else ldap_password
        self.ldap_port = ldap_port
        self.mongo_password = generate_password() if mongo_password is None else mongo_password
        self.mongo_port = mongo_port
        self.max_web_ui_ram = max_web_ui_ram
        self.max_mongo_ram = max_mongo_ram
        self.include_swagger_container = include_swagger_container


########################################################################################################################

CLUSTERS = [

    ClusterSetup(url_prefix='demo',
                 docker_repo_name_suffix='ara',
                 cluster_uri_name='demo',
                 release_tier_tag='prod',
                 ldap_password='6t3mvTRAoudMM0X4HhbIPII4PemEyZux',
                 ldap_port=3891,
                 mongo_password='VCixlUKkMkfk3Jfgn2xpYJtbKZ7EI3A4',
                 mongo_port=27022,
                 max_web_ui_ram='4g',
                 max_mongo_ram='4g',
                 include_swagger_container=True),
    ClusterSetup(url_prefix='ncsu',
                 docker_repo_name_suffix='ncsu',
                 cluster_uri_name='ncsu',
                 release_tier_tag='prod',
                 ldap_password='7gmmRzFch7mSgNHPot3rjwBERQZXqLtX',
                 ldap_port=3892,
                 mongo_password='6vizQoneTiMZlQFJZ9xPBTWWSd4EATJ3',
                 mongo_port=27023,
                 max_web_ui_ram='4g',
                 max_mongo_ram='4g',
                 include_swagger_container=True),
    ClusterSetup(url_prefix='dev',
                 docker_repo_name_suffix='ara',
                 cluster_uri_name='ara-dev',
                 release_tier_tag='dev',
                 ldap_password='LPTdNpLoICJLETQx2bF6lhBIuRRhJKi9',
                 ldap_port=3901,
                 mongo_password='dLakGjsU4Mx8uqTEsbfCUqm5oVQjWG3U',
                 mongo_port=27017,
                 max_web_ui_ram='2g',
                 max_mongo_ram='2g',
                 include_swagger_container=True),
    ClusterSetup(url_prefix='ncsu-dev',
                 docker_repo_name_suffix='ncsu',
                 cluster_uri_name='ncsu-dev',
                 release_tier_tag='dev',
                 ldap_password='8zuHSTxBsC94hEnk4KmcjTnC08ClzGne',
                 ldap_port=3902,
                 mongo_password='BzPHFxMM3gMbn68uKSW1OoBFwjmzr8YT',
                 mongo_port=27018,
                 max_web_ui_ram='2g',
                 max_mongo_ram='2g',
                 include_swagger_container=False),
]

READ_ONLY = False

RELATIVE_PATH_TO_DOCKER_DIR = '../../docker'

PASSWORD_LENGTH = 32
PASSWORD_CHARSET = 'abcdefghijklmnopqrstuvwxyz' + 'ABCDEFGHIJKLMNOPQRSTUVWXYZ' + '0123456789'

DOCKER_REGISTRY = '660195182610.dkr.ecr.us-east-1.amazonaws.com'

DOCKER_COMPOSE_DIVIDER_LINE = '##########################################################################################' \
                              '##########################################################################################\n'

DOCKER_COMPOSE_CLUSTERS_FILENAME = 'docker-compose-clusters.yaml'

NGINX_CONFIG_DIVIDER_LINE = '\t####################################################################################################'

NGINX_CLUSTERS_CONFIG_FILE_PATH = '../../docker/compose/aws/reverse-proxy/aws-full.conf.tmpl'


########################################################################################################################


def main():
    docker_compose_services_lines = []
    docker_compose_volumes_lines = []

    updated_config_lines = []
    for cluster_setup in CLUSTERS:
        generated_cluster_setup = generate_cluster(cluster_setup)

        docker_compose_services_lines.append('\n')
        docker_compose_services_lines.append(DOCKER_COMPOSE_DIVIDER_LINE)
        docker_compose_services_lines.append('# %s cluster\n' % cluster_setup.cluster_uri_name)
        docker_compose_services_lines.append(DOCKER_COMPOSE_DIVIDER_LINE)
        docker_compose_services_lines.append('\n')

        docker_compose_services_lines.extend(generated_cluster_setup[0])
        docker_compose_volumes_lines.extend(generated_cluster_setup[1])
        updated_config_lines.append(generated_cluster_setup[2])

    docker_compose_lines = [
        DOCKER_COMPOSE_DIVIDER_LINE,
        '#\n',
        '# Note: This file is generated by the script: ' + os.path.basename(__file__) + '\n',
        '#\n',
        DOCKER_COMPOSE_DIVIDER_LINE,
        '\n',
        'version: \'3\'\n',
        '\n',
        DOCKER_COMPOSE_DIVIDER_LINE,
        '\n',
        'networks:\n',
        '  imaginationsupport:\n',
        '\n',
        DOCKER_COMPOSE_DIVIDER_LINE,
        '\n',
        'services:\n',
    ]
    docker_compose_lines.extend(docker_compose_services_lines)
    docker_compose_lines.append('\n')
    docker_compose_lines.append(DOCKER_COMPOSE_DIVIDER_LINE)
    docker_compose_lines.append('\n')
    docker_compose_lines.append('volumes:\n')
    docker_compose_lines.extend(docker_compose_volumes_lines)
    docker_compose_lines.append('\n')
    docker_compose_lines.append(DOCKER_COMPOSE_DIVIDER_LINE)

    path_to_docker_compose_file = Path(RELATIVE_PATH_TO_DOCKER_DIR) / 'compose' / 'aws' / DOCKER_COMPOSE_CLUSTERS_FILENAME
    if not READ_ONLY:
        output_file = open(path_to_docker_compose_file, 'w', newline="\n")
        output_file.writelines(docker_compose_lines)
        output_file.close()

    print()
    print('Updated config:')
    print()
    print('\n'.join(updated_config_lines))
    print()
    print('Docker-compose alias:')

    docker_compose_entries = ['docker-compose-support.yaml', DOCKER_COMPOSE_CLUSTERS_FILENAME]
    print()
    print('\talias dc="docker-compose -p scenarioexplorer --compatibility -f %s"' % ' -f '.join(docker_compose_entries))
    print()

    reverse_proxy_lines = ['']
    for cluster_setup in CLUSTERS:
        reverse_proxy_lines.extend(generate_reverse_proxy_config(cluster_setup.url_prefix, cluster_setup.cluster_uri_name))
    update_template(NGINX_CLUSTERS_CONFIG_FILE_PATH, 'GENERATED CONFIG', reverse_proxy_lines)


# output_file = open(path_to_docker_compose_file, 'w', newline="\n")


########################################################################################################################

def generate_password():
    new_password = ''

    for i in range(0, PASSWORD_LENGTH):
        new_password += random.choice(PASSWORD_CHARSET)

    return new_password


########################################################################################################################

def generate_cluster(cluster_setup):
    print('Generating cluster: %s' % cluster_setup.cluster_uri_name)

    web_ui_uri = 'https://' + cluster_setup.url_prefix + '/scenarioexplorer'
    web_user_support_uri = 'https://' + cluster_setup.url_prefix + '/user-support'

    # generate the docker-compose file lines
    docker_compose_services_lines = [
        '  ldap-%s:\n' % cluster_setup.cluster_uri_name,
        '    image: %s/ldap:%s\n' % (DOCKER_REGISTRY, cluster_setup.release_tier_tag),
        '    container_name: ldap-%s\n' % cluster_setup.cluster_uri_name,
        '    restart: always\n',
        '    ports:\n',
        '      - "%d:389"\n' % cluster_setup.ldap_port,
        '    volumes:\n',
        '      - ldap-%s-config:/etc/openldap-dist\n' % cluster_setup.cluster_uri_name,
        '      - ldap-%s-data:/var/lib/openldap\n' % cluster_setup.cluster_uri_name,
        '    env_file:\n',
        '      - env/aws-%s.env\n' % cluster_setup.cluster_uri_name,
        '#    environment:\n',
        '#      - USE_LDAP_LOGGING=true\n',
        '    networks:\n',
        '      - imaginationsupport\n',
        '\n',
        '##########################################################################################\n',
        '\n',
        '  web-ui-%s:\n' % cluster_setup.cluster_uri_name,
        '    image: %s/web-ui-%s:%s\n' % (DOCKER_REGISTRY, cluster_setup.docker_repo_name_suffix, cluster_setup.release_tier_tag),
        '    container_name: web-ui-%s\n' % cluster_setup.cluster_uri_name,
        '    restart: always\n',
        '    depends_on:\n',
        '      - ldap-%s\n' % cluster_setup.cluster_uri_name,
        '      - mongo-db-%s\n' % cluster_setup.cluster_uri_name,
        '    env_file:\n',
        '      - env/aws-%s.env\n' % cluster_setup.cluster_uri_name,
        '    deploy:\n'
        '      limits:\n'
        '        memory: %s\n' % cluster_setup.max_web_ui_ram,
        '    volumes:\n',
        '      - /docker-data/volumes/%s-web-ui:/usr/local/tomcat/logs\n' % cluster_setup.cluster_uri_name,
        '    networks:\n',
        '      - imaginationsupport\n',
        '\n',
        '##########################################################################################\n',
        '\n',
        '  mongo-db-%s:\n' % cluster_setup.cluster_uri_name,
        '    image: %s/mongo-db:%s\n' % (DOCKER_REGISTRY, cluster_setup.release_tier_tag),
        '    container_name: mongo-db-%s\n' % cluster_setup.cluster_uri_name,
        '    restart: always\n',
        '    ports:\n',
        '      - "%d:27017"\n' % cluster_setup.mongo_port,
        '    volumes:\n',
        '      - mongo-db-%s-data:/data/db\n' % cluster_setup.cluster_uri_name,
        '    env_file:\n',
        '      - env/aws-%s.env\n' % cluster_setup.cluster_uri_name,
        '    deploy:\n'
        '      limits:\n'
        '        memory: %s\n' % cluster_setup.max_mongo_ram,
        '    networks:\n',
        '      - imaginationsupport\n',
    ]

    if cluster_setup.include_swagger_container:
        docker_compose_services_lines.extend([
            '\n',
            '##########################################################################################\n',
            '\n',
            '  swagger-ui-%s:\n' % cluster_setup.cluster_uri_name,
            '    image: %s/swagger-ui:%s\n' % (DOCKER_REGISTRY, cluster_setup.release_tier_tag),
            '    container_name: swagger-ui-%s\n' % cluster_setup.cluster_uri_name,
            '    restart: always\n',
            '    environment:\n',
            '      - API_URL=/scenarioexplorer/swagger-v2.yaml\n',
            '    depends_on:\n',
            '      - web-ui-%s\n' % cluster_setup.cluster_uri_name,
            '    networks:\n',
            '      - imaginationsupport\n',
        ])

    docker_compose_volumes_lines = [
        '  mongo-db-%s-data:\n' % cluster_setup.cluster_uri_name,
        '  ldap-%s-config:\n' % cluster_setup.cluster_uri_name,
        '  ldap-%s-data:\n' % cluster_setup.cluster_uri_name,
    ]

    # generate the env file lines
    env_lines = [
        '################################################################################\n',
        '# MongoDB - internal\n',
        '\n',
        'SE_MONGODB_HOSTNAME=mongo-db-%s\n' % cluster_setup.cluster_uri_name,
        'SE_MONGODB_PORT=27017\n',
        'SE_MONGODB_DATABASE=scenarioexplorer\n',
        'SE_MONGODB_USERNAME=mongoadmin\n',
        'SE_MONGODB_PASSWORD=%s\n' % cluster_setup.mongo_password,
        '\n',
        '################################################################################\n',
        '# MongoDB - container\n',
        '\n',
        '# use the settings built into mongo docker container\n',
        '# https://hub.docker.com/_/mongo/\n',
        '\n',
        'MONGO_INITDB_ROOT_USERNAME=mongoadmin\n',
        'MONGO_INITDB_ROOT_PASSWORD=%s\n' % cluster_setup.mongo_password,
        '\n',
        '################################################################################\n',
        '# LDAP\n',
        '\n',
        'SE_LDAP_ROOT_DN=uid=scenario-explorer-admin,dc=imaginationsupport,dc=com\n',
        'SE_LDAP_ROOT_PW=%s\n' % cluster_setup.ldap_password,
        'SE_LDAP_BASE_DN=dc=imaginationsupport,dc=com\n',
        'SE_LDAP_PROTOCOL=ldap\n',
        'SE_LDAP_SERVER=ldap-%s\n' % cluster_setup.cluster_uri_name,
        'SE_LDAP_PORT=389\n',
        '\n',
        '################################################################################\n',
        '# Tomcat\n',
        '\n',
        'SE_TOMCAT_USERNAME=lasadmin\n',
        'SE_TOMCAT_PASSWORD=%s\n' % cluster_setup.mongo_password,  # TODO this should actually be a different password, and remove altogether when deployed on production
        '\n',
        '################################################################################\n',
        '# SMTP\n',
        '\n',
        'SE_SMTP_HOSTNAME=box867.bluehost.com\n',
        'SE_SMTP_PORT=465\n',
        'SE_SMTP_USERNAME=noreply@imaginationsupport.com\n',
        'SE_SMTP_PASSWORD=D0notUs3\n',
        'SE_SMTP_USE_SSL=true\n',
        'SE_SMTP_FROM_EMAIL=noreply@imaginationsupport.com\n',
        'SE_SMTP_FROM_NAME=Scenario Explorer\n',
        'SE_SMTP_BCC_ADDRESSES=mlyle@ara.com\n',
        '\n',
        '################################################################################\n',
        '# URLs\n',
        '\n',
        'SE_URL_WEB_UI=%s\n' % web_ui_uri,
        'SE_URL_WEB_USER_SUPPORT=%s\n' % web_user_support_uri,
        '\n',
        'SE_CLUSTER_NAME=%s\n' % cluster_setup.cluster_uri_name,
        '\n',
        '################################################################################\n',
    ]

    # write the env file
    path_to_env_file = Path(RELATIVE_PATH_TO_DOCKER_DIR) / 'compose' / 'aws' / 'env' / ('aws-%s.env' % cluster_setup.cluster_uri_name)
    if not READ_ONLY:
        output_file = open(path_to_env_file, 'w', newline="\n")
        output_file.writelines(env_lines)
        output_file.close()

    command_lines = (
        'url_prefix=\'%s\'' % cluster_setup.url_prefix,
        'docker_repo_name_suffix=\'%s\'' % cluster_setup.docker_repo_name_suffix,
        'cluster_uri_name=\'%s\'' % cluster_setup.cluster_uri_name,
        'release_tier_tag=\'%s\'' % cluster_setup.release_tier_tag,
        'ldap_password=\'%s\'' % cluster_setup.ldap_password,
        'ldap_port=%d' % cluster_setup.ldap_port,
        'mongo_password=\'%s\'' % cluster_setup.mongo_password,
        'mongo_port=%d' % cluster_setup.mongo_port,
        'max_web_ui_ram=\'%s\'' % cluster_setup.max_web_ui_ram,
        'max_mongo_ram=\'%s\'' % cluster_setup.max_mongo_ram,
        'include_swagger_container=%s' % cluster_setup.include_swagger_container,
    )

    return docker_compose_services_lines, docker_compose_volumes_lines, '\tClusterSetup(%s),' % ',\n\t             '.join(command_lines)


########################################################################################################################


def generate_reverse_proxy_config(url_prefix, cluster_uri_name):
    return [
        NGINX_CONFIG_DIVIDER_LINE,
        '\t# %s - HTTP' % cluster_uri_name,
        '',
        '\tserver',
        '\t{',
        '\t\tlisten 80;',
        '\t\tserver_name %s.imaginationsupport.com;' % url_prefix,
        '',
        '\t\tinclude http-redirect-https.conf;',
        '\t}',
        '\t',
        NGINX_CONFIG_DIVIDER_LINE,
        '\t# %s - HTTPS' % cluster_uri_name,
        '',
        '\tserver',
        '\t{',
        '\t\tlisten 443 ssl;',
        '\t\tserver_name %s.imaginationsupport.com;' % url_prefix,
        '',
        '\t\t# HTTPS config',
        '\t\tssl_certificate /etc/letsencrypt/live/%s.imaginationsupport.com/fullchain.pem;' % url_prefix,
        '\t\tssl_certificate_key /etc/letsencrypt/live/%s.imaginationsupport.com/privkey.pem;' % url_prefix,
        '\t\tinclude ssl-config.conf;',
        '',
        '\t\t# Custom error pages',
        '\t\tinclude error-pages.conf;',
        '',
        '\t\tlocation /',
        '\t\t{',
        '\t\t\tproxy_intercept_errors on;',
        '\t\t\tproxy_connect_timeout 5;',
        '\t\t\tproxy_read_timeout 240;',
        '\t\t\tproxy_pass http://web-ui-%s:8080;' % cluster_uri_name,
        '\t\t}',
        '\t}',
        ''
    ]


########################################################################################################################


def update_template(file_path, template_keyword, new_lines):
    with open(file_path) as input_file:
        lines = [line.rstrip() for line in input_file.readlines()]

    start_line = None
    end_line = None
    line_index = 0
    for line in lines:
        # print(line)

        temp = line.upper().find(template_keyword + ' START')
        if temp > -1:
            if not start_line:
                start_line = line_index + 2
            else:
                raise Exception('Multiple start template markers found!')

        temp = line.upper().find(template_keyword + ' END')
        if temp > -1:
            if not end_line:
                end_line = line_index - 2
            else:
                raise Exception('Multiple end template markers found!')

        line_index += 1

    # print('start: %s' % start_line)
    # print('end:   %s' % end_line)

    if not start_line:
        raise Exception('Start template markers not found!')
    if not end_line:
        raise Exception('End template markers not found!')
    if start_line >= end_line:
        raise Exception('Start template marker must be before the end marker!')

    # print('\n'.join(lines[start_line:end_line - start_line]))

    lines[start_line:end_line - start_line] = new_lines

    if not READ_ONLY:
        output_file = open(file_path, 'w', newline="\n")
        output_file.write('\n'.join(lines))
        output_file.close()


########################################################################################################################

if __name__ == "__main__":
    main()

########################################################################################################################
