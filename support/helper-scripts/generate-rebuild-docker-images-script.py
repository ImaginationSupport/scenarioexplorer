#!/usr/bin/env python3

import os
from pathlib import Path
import re

########################################################################################################################
#
# NOTE:  The should be run from the "console-tools/working-dir" folder
#
########################################################################################################################

READ_ONLY = False

REBUILD_IMAGES_FILENAME = 'rebuild-docker-images.sh'

PUSH_ALL_FILENAME = 'push-all-docker-images-to-registry.sh'

PULL_ALL_FILENAME = 'pull-all-docker-images-from-registry.sh'

DOCKER_ANT_BUILD_FILENAME = 'build-docker.ant'

DOCKER_REGISTRY = '660195182610.dkr.ecr.us-east-1.amazonaws.com'

DOCKER_REPO_NAME = 'imaginationsupport'

RELATIVE_PATH_TO_DOCKER_DIR = '../../docker'

ANT_BUILD_TARGET_NAME_PREFIX = 'build-docker-image-'
ANT_PUSH_TARGET_NAME_PREFIX = 'push-docker-image-'
ANT_CLEAN_DOCKER_TARGET_NAME = 'clean-docker'

IMAGES = [
    # (folder_name, display_name, include_in_bootstrap, is_web_ui_target, web_ui_target_needs_auth),

    ('ldap', 'LDAP', True, False, False),
    ('mongo-db', 'Mongo DB', True, False, False),

    ('teamcity-agent', 'Teamcity Agent', False, False, False),
    ('teamcity-database', 'Teamcity Database', False, False, False),

    ('tomcat-base', 'Tomcat Base', True, False, False),
    ('web-ui-ara', 'ARA Web UI', False, True, True),
    ('web-ui-ncsu', 'NCSU Web UI', False, True, True),

    ('reverse-proxy', 'Reverse Proxy', True, False, False),

    ('dev-console', 'Dev Console', True, False, False),

    ('swagger-ui', 'Swagger UI', False, False, False),
]

BASH_DIVIDER_LINE = '##########################################################################################' \
                    '##########################################################################################\n'

ANT_DIVIDER_LINE = '\t<!-- ###################################################################################' \
                   '#################################################################################### -->\n\n'


########################################################################################################################

def main():
    # support_helper_scripts_dir = Path(os.path.dirname(os.path.realpath(__file__)))
    # print(support_helper_scripts_dir)

    support_dir = Path(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))
    # print(support_dir)

    # generate_rebuild_script(support_helper_scripts_dir / REBUILD_IMAGES_FILENAME)

    # generate_push_all_script(support_helper_scripts_dir / PUSH_ALL_FILENAME)

    # generate_pull_all_script(support_helper_scripts_dir / PULL_ALL_FILENAME)

    generate_docker_ant_build(support_dir / DOCKER_ANT_BUILD_FILENAME)

    print('All tasks complete.')
    return


########################################################################################################################

def generate_rebuild_script(output_path):
    lines = [
        '#!/usr/bin/env bash\n',
        '\n',
        'set -eu\n',
        '\n',
        'DIVIDER_LINE="================================================================================"\n',
        'DIVIDER_TOP="\\n${DIVIDER_LINE}"\n',
        'DIVIDER_BOTTOM="${DIVIDER_LINE}\\n"\n',
        'DOCKER_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}" )/%s" && pwd )"\n' % RELATIVE_PATH_TO_DOCKER_DIR,
        '\n',
        'BOOTSTRAP=false\n',
        'if [ $# -gt 0 ]; then\n',
        '\tif [ $# -eq 1 ] && [ "$1" == "--bootstrap" ]; then\n',
        '\t\tBOOTSTRAP=true\n',
        '\telse\n',
        '\t\tfor IMAGE_NAME in "$@"\n',
        '\t\tdo\n',
        '\t\t\techo -e ${DIVIDER_TOP}\n',
        '\t\t\techo "    Building image: ${IMAGE_NAME}..."\n',
        '\t\t\techo -e ${DIVIDER_BOTTOM}\n',
        '\t\t\tdocker build -t %s/${IMAGE_NAME} ${DOCKER_DIR}/images/${IMAGE_NAME}\n' % DOCKER_REPO_NAME,
        '\t\tdone\n',
        '\t\texit 0\n',
        '\tfi\n',
        'fi\n',
        '\n',
        '# otherwise rebuild all images...\n'
        '\n',
    ]

    i = 1
    for image in IMAGES:
        image_name = image[0]
        image_description = image[1]
        include_in_bootstrap = image[2]

        lines.append(BASH_DIVIDER_LINE)
        lines.append('\n')
        lines.append('echo -e ${DIVIDER_TOP}\n')
        lines.append('echo -e "    Building image %d of %d: %s"\n' % (i, len(IMAGES), image_description))
        lines.append('echo -e ${DIVIDER_BOTTOM}\n')
        if include_in_bootstrap:
            lines.append('docker build -t %s/%s ${DOCKER_DIR}/images/%s\n' % (DOCKER_REPO_NAME, image_name, image_name))
        else:
            lines.append('if [ ${BOOTSTRAP} = true ]; then\n')
            lines.append('\techo "(Skipping in bootstrap build)"\n')
            lines.append('else\n')
            lines.append('\tdocker build -t %s/%s ${DOCKER_DIR}/images/%s\n' % (DOCKER_REPO_NAME, image_name, image_name))
            lines.append('fi\n')
        lines.append('\n')

        i = i + 1

    lines.append(BASH_DIVIDER_LINE)
    lines.append('\n')
    lines.append('echo -e ${DIVIDER_TOP}\n')
    lines.append('echo "done."\n')
    lines.append('echo -e ${DIVIDER_BOTTOM}\n')
    lines.append('echo ""\n')
    lines.append('\n')
    lines.append(BASH_DIVIDER_LINE)

    # now generate the file (or display to STDOUT if in read-only mode)
    if READ_ONLY:
        print('\t' + '\t'.join(lines))
        print('\n')
    else:
        output_file = open(output_path, 'w', newline="\n")
        output_file.writelines(lines)
        output_file.close()

    return


########################################################################################################################

def generate_push_all_script(output_path):
    lines = [
        '#!/usr/bin/env bash\n',
        '\n',
        'set -eu\n',
        '\n',
        BASH_DIVIDER_LINE,
        '\n',
        'RELEASE_TIER="dev" # dev / ncsu-prod\n',
        '\n',
        BASH_DIVIDER_LINE,
        '\n',
        'echo ""\n',
        'echo "Tagging release tier: ${RELEASE_TIER}"\n',
        'echo ""\n',
        '\n',
        'sleep 3\n',
        '\n',
        BASH_DIVIDER_LINE,
        '\n',
        'if [ $# -gt 0 ]; then\n',
        '\tfor IMAGE_NAME in "$@"\n',
        '\tdo\n',
        '\t\techo "Pushing image: ${IMAGE_NAME}..."\n',
        '\t\tdocker tag %s/${IMAGE_NAME} %s/${IMAGE_NAME}:${RELEASE_TIER}\n' % (DOCKER_REPO_NAME, DOCKER_REGISTRY),
        '\t\tdocker push %s/${IMAGE_NAME}:${RELEASE_TIER}\n' % DOCKER_REGISTRY,
        '\t\tdocker tag %s/${IMAGE_NAME} %s/${IMAGE_NAME}:latest\n' % (DOCKER_REPO_NAME, DOCKER_REGISTRY),
        '\t\tdocker push %s/${IMAGE_NAME}:latest\n' % DOCKER_REGISTRY,
        '\t\techo ""\n',
        '\tdone\n',
        '\texit 0\n',
        'fi\n',
        '\n',
        BASH_DIVIDER_LINE,
        '# otherwise push all images...\n'
        '\n',
    ]

    i = 1
    num_images = len(IMAGES)
    for image in IMAGES:
        image_name = image[0]
        image_description = image[1]
        # include_in_bootstrap = image[2]

        lines.append('echo "Image %d of %d: %s..."\n' % (i, num_images, image_description))
        lines.append('docker tag %s/%s %s/%s:${RELEASE_TIER}\n' % (DOCKER_REPO_NAME, image_name, DOCKER_REGISTRY, image_name))
        lines.append('docker push %s/%s:${RELEASE_TIER}\n' % (DOCKER_REGISTRY, image_name))
        lines.append('docker tag %s/%s %s/%s:latest\n' % (DOCKER_REPO_NAME, image_name, DOCKER_REGISTRY, image_name))
        lines.append('docker push %s/%s:latest\n' % (DOCKER_REGISTRY, image_name))
        lines.append('echo ""\n')
        lines.append('\n')

        i = i + 1

    lines.append(BASH_DIVIDER_LINE)

    # now generate the file (or display to STDOUT if in read-only mode)
    if READ_ONLY:
        print('\t' + '\t'.join(lines))
        print('\n')
    else:
        output_file = open(output_path, 'w', newline="\n")
        output_file.writelines(lines)
        output_file.close()

    return


########################################################################################################################

# def generate_pull_all_script(output_path):
#     lines = [
#         '#!/usr/bin/env bash\n',
#         '\n',
#         'set -eu\n',
#         '\n',
#         BASH_DIVIDER_LINE,
#         '\n',
#         'RELEASE_TIER="dev" # dev / prod-ncsu / v0.0-2018-01-01\n',
#         '\n',
#         BASH_DIVIDER_LINE,
#         '\n',
#         'if [ $# -gt 0 ]; then\n',
#         '\tfor IMAGE_NAME in "$@"\n',
#         '\tdo\n',
#         '\t\techo "Pulling image: ${IMAGE_NAME}:${RELEASE_TIER}..."\n',
#         '\t\tdocker pull %s/${IMAGE_NAME}:${RELEASE_TIER}\n' % DOCKER_REGISTRY,
#         '\t\tdocker tag %s/${IMAGE_NAME}:${RELEASE_TIER} %s/${IMAGE_NAME}:${RELEASE_TIER}\n' % (DOCKER_REPO_NAME, DOCKER_REGISTRY),
#         '\t\techo ""\n',
#         '\tdone\n',
#         '\texit 0\n',
#         'fi\n',
#         '\n',
#         BASH_DIVIDER_LINE,
#         '# otherwise push all images...\n'
#         '\n',
#     ]
#
#     i = 1
#     num_images = len(IMAGES)
#     for image in IMAGES:
#         image_name = image[0]
#         image_description = image[1]
#         # include_in_bootstrap = image[2]
#
#         lines.append('echo "Image %d of %d: %s..."\n' % (i, num_images, image_description))
#         lines.append('docker pull %s/%s:${RELEASE_TIER}\n' % (DOCKER_REGISTRY, image_name))
#         lines.append('docker tag %s/%s:${RELEASE_TIER} %s/%s:${RELEASE_TIER}\n' % (DOCKER_REGISTRY, image_name, DOCKER_REPO_NAME, image_name))
#         lines.append('echo ""\n')
#         lines.append('\n')
#
#         i = i + 1
#
#     lines.append(BASH_DIVIDER_LINE)
#
#     # now generate the file (or display to STDOUT if in read-only mode)
#     if READ_ONLY:
#         print('\t' + '\t'.join(lines))
#         print('\n')
#     else:
#         output_file = open(output_path, 'w', newline="\n")
#         output_file.writelines(lines)
#         output_file.close()
#
#     return


########################################################################################################################


def generate_docker_ant_build(output_path):
    lines = [
        '<project name="LAS - Scenario Explorer - Docker">\n',
        '\n'
        '\t<!--\n',
        '\t     ####################################################################################################\n',
        '\t     #                                                                                                  #\n',
        '\t     # IMPORTANT: This file is automatically generated from the helper script:                          #\n',
        '\t     #            %-85s #\n' % os.path.basename(__file__),
        '\t     #                                                                                                  #\n',
        '\t     ####################################################################################################\n',
        '\t-->\n',
        '\n',
        '\t<property name="docker.registry" value="%s" />\n' % DOCKER_REGISTRY,
        '\n',
        ANT_DIVIDER_LINE,
        '\t<!-- Clean the docker image folders -->\n',
        '\t<target name="%s" description="Cleans docker image folders">\n' % ANT_CLEAN_DOCKER_TARGET_NAME
    ]

    first_entry = True
    for image in IMAGES:
        image_name = image[0]
        is_web_ui_target = image[3]

        if is_web_ui_target:
            if first_entry:
                first_entry = False
            else:
                lines.append('\n')

            lines.append('\t\t<echo message="Cleaning docker image %s..." />\n' % image_name)
            lines.append('\t\t<delete verbose="true">\n')
            lines.append('\t\t\t<fileset dir="${docker.image.path}/%s" includes="*.war" />\n' % image_name)
            lines.append('\t\t\t<fileset dir="${docker.image.path}/%s" includes="*.tar.gz" />\n' % image_name)
            lines.append('\t\t</delete>\n')

    lines.append('\t</target>\n')
    lines.append('\n')

    lines.append(ANT_DIVIDER_LINE)

    # add the individual build targets
    image_names = []
    for image in IMAGES:
        image_name = image[0]
        image_description = image[1]
        # include_in_bootstrap = image[2]
        is_web_ui_target = image[3]

        base_image_name = get_docker_base_image_name(image_name)

        ant_depends_targets = ['create-build-tag']
        if is_web_ui_target:
            ant_depends_targets.insert(0, 'prep-docker-build-%s' % image_name)
            ant_depends_targets.append('build-docker-image-tomcat-base')

        # always add clean and clean-docker as the initial pre-requisites
        ant_depends_targets.insert(0, ANT_CLEAN_DOCKER_TARGET_NAME)
        ant_depends_targets.insert(0, 'clean')

        lines.append('\t<!-- Build: %s -->\n' % image_name)
        lines.append('\t<target name="%s%s" description="Builds the %s docker image" depends="%s">\n' %
                     (ANT_BUILD_TARGET_NAME_PREFIX, image_name, image_description, '\n\t\t' + ',\n\t\t'.join(ant_depends_targets)))

        if not base_image_name.startswith('imaginationsupport/'):
            print('Base image for %s: %s' % (image_name, base_image_name))
            lines.append('\t\t<antcall target="pull-base-image">\n')
            lines.append('\t\t\t<param name="image.name" value="%s" />\n' % base_image_name)
            lines.append('\t\t</antcall>\n')
            lines.append('\n')

        lines.append('\t\t<antcall target="run-build-docker-image">\n')
        lines.append('\t\t\t<param name="repo.name" value="%s" />\n' % DOCKER_REPO_NAME)
        lines.append('\t\t\t<param name="image.name" value="%s" />\n' % image_name)
        lines.append('\t\t\t<param name="image.path" value="${docker.image.path}/%s" />\n' % image_name)
        lines.append('\t\t</antcall>\n')

        lines.append('\t</target>\n')
        lines.append('\n')

        image_names.append(ANT_BUILD_TARGET_NAME_PREFIX + image_name)

    lines.append(ANT_DIVIDER_LINE)

    # add the combined build target
    lines.append('\t<target name="build-docker-images" description="Builds all docker images" depends="\n')
    lines.append('\t\t%s\n' % ',\n\t\t'.join(image_names))
    lines.append('\t\t" />\n')
    lines.append('\n')

    lines.append(ANT_DIVIDER_LINE)

    # add the individual push targets
    for image in IMAGES:
        image_name = image[0]
        # image_description = image[1]
        # include_in_bootstrap = image[2]
        # is_web_ui_target = image[3]
        # web_ui_target_needs_auth = image[4]

        release_tiers = ['dev', 'prod']

        for release_tier in release_tiers:
            lines.append('\t<!-- Push: %s -->\n' % image_name)
            lines.append('\t<target name="%s%s-%s" description="Pushes the docker image to the AWS registry as :%s" depends="create-build-tag">\n' % (
                ANT_PUSH_TARGET_NAME_PREFIX, image_name, release_tier, release_tier))
            lines.append('\t\t<antcall target="run-push-docker-image">\n')
            lines.append('\t\t\t<param name="repo.name" value="%s" />\n' % DOCKER_REPO_NAME)
            lines.append('\t\t\t<param name="image.name" value="%s" />\n' % image_name)
            lines.append('\t\t\t<param name="image.path" value="${docker.image.path}/%s" />\n' % image_name)
            lines.append('\t\t\t<param name="build.tag" value="${docker.build.tag}" />\n')
            lines.append('\t\t\t<param name="release.tier.tag" value="%s" />\n' % release_tier)
            lines.append('\t\t</antcall>\n')
            lines.append('\t</target>\n')
            lines.append('\n')

    lines.append(ANT_DIVIDER_LINE)

    for image in IMAGES:
        image_name = image[0]
        # image_description = image[1]
        # include_in_bootstrap = image[2]
        is_web_ui_target = image[3]
        web_ui_target_needs_auth = image[4]

        if is_web_ui_target:

            target_name_split = image_name.split('-')
            theme_name = target_name_split[2]

            depends = ['use-web-ui-theme-%s' % theme_name, 'build-web-ui-war', 'build-web-ui-tomcat-server-jars-tar-gz']

            if web_ui_target_needs_auth:
                # add use-auth first
                depends.insert(0, 'use-auth')

                # and the user-support ones at the end
                depends.append('use-web-user-support-theme-%s' % theme_name)
                depends.append('build-web-user-support-war')
                depends.append('build-web-user-support-tomcat-server-jars-tar-gz')

            lines.append('\t<!-- Prep: %s -->\n' % image_name)
            lines.append('\t<target name="prep-docker-build-%s" depends="%s">\n' % (image_name, '\n\t\t' + ',\n\t\t'.join(depends)))
            lines.append('\t\t<copy file="${web-ui.war.path}" tofile="${docker.image.%s.path}/web-ui.war" />\n' % image_name)
            lines.append('\t\t<copy file="${web-ui.tomcat.jars.tar.gz.path}" tofile="${docker.image.%s.path}/tomcat-jars-web-ui.tar.gz" />\n' % image_name)
            if web_ui_target_needs_auth:
                lines.append('\t\t<copy file="${web-user-support.war.path}" tofile="${docker.image.%s.path}/web-user-support.war" />\n' % image_name)
                lines.append('\t\t<copy file="${web-user-support.tomcat.jars.tar.gz.path}" tofile="${docker.image.%s.path}/tomcat-jars-web-user-support.tar.gz" />\n' % image_name)
            lines.append('\t</target>\n')
            lines.append('\n')

    lines.append(ANT_DIVIDER_LINE)

    # add the build helper functions
    lines.append('\t<!-- Helper: run-build-docker-image -->\n')
    lines.append('\t<target name="run-build-docker-image">\n')
    lines.append('\t\t<fail message="Missing: repo.name" unless="repo.name" />\n')
    lines.append('\t\t<fail message="Missing: image.name" unless="image.name" />\n')
    lines.append('\t\t<fail message="Missing: image.path" unless="image.path" />\n')
    lines.append('\n')
    lines.append('\t\t<echo message="Building: ${repo.name}/${image.name}" />\n')
    lines.append('\t\t<exec executable="docker" failonerror="true">\n')
    lines.append('\t\t\t<arg value="build" />\n')
    lines.append('\t\t\t<arg value="-t" />\n')
    lines.append('\t\t\t<arg value="${repo.name}/${image.name}" />\n')
    lines.append('\t\t\t<arg value="${image.path}" />\n')
    lines.append('\t\t</exec>\n')
    lines.append('\t</target>\n')
    lines.append('\n')

    lines.append(ANT_DIVIDER_LINE)

    # add the push helper functions
    lines.append('\t<!-- Helper: run-push-docker-image -->\n')
    lines.append('\t<target name="run-push-docker-image">\n')
    lines.append('\t\t<fail message="Missing: repo.name" unless="repo.name" />\n')
    lines.append('\t\t<fail message="Missing: image.name" unless="image.name" />\n')
    lines.append('\t\t<fail message="Missing: docker.registry" unless="docker.registry" />\n')
    # lines.append('\t\t<fail message="Missing: build.tag" unless="build.tag" />\n')
    lines.append('\t\t<fail message="Missing: release.tier.tag" unless="release.tier.tag" />\n')
    lines.append('\n')
    # lines.append('\t\t<!-- Tagging as build tag -->\n')
    # lines.append('\t\t<echo message="Tagging: ${repo.name}/${image.name} with :${build.tag}" />\n')
    # lines.append('\t\t<exec executable="docker" failonerror="true">\n')
    # lines.append('\t\t\t<arg value="tag" />\n')
    # lines.append('\t\t\t<arg value="${repo.name}/${image.name}" />\n')
    # lines.append('\t\t\t<arg value="${docker.registry}/${image.name}:${build.tag}" />\n')
    # lines.append('\t\t</exec>\n')
    # lines.append('\t\t<exec executable="docker" failonerror="true">\n')
    # lines.append('\t\t\t<arg value="push" />\n')
    # lines.append('\t\t\t<arg value="${docker.registry}/${image.name}:${build.tag}" />\n')
    # lines.append('\t\t</exec>\n')
    # lines.append('\n')
    lines.append('\t\t<!-- Tagging as release tier -->\n')
    lines.append('\t\t<echo message="Tagging: ${repo.name}/${image.name} with :${release.tier.tag}" />\n')
    lines.append('\t\t<exec executable="docker" failonerror="true">\n')
    lines.append('\t\t\t<arg value="tag" />\n')
    lines.append('\t\t\t<arg value="${repo.name}/${image.name}" />\n')
    lines.append('\t\t\t<arg value="${docker.registry}/${image.name}:${release.tier.tag}" />\n')
    lines.append('\t\t</exec>\n')
    lines.append('\t\t<exec executable="docker" failonerror="true">\n')
    lines.append('\t\t\t<arg value="push" />\n')
    lines.append('\t\t\t<arg value="${docker.registry}/${image.name}:${release.tier.tag}" />\n')
    lines.append('\t\t</exec>\n')
    lines.append('\n')
    lines.append('\t\t<!-- Tagging as :latest -->\n')
    lines.append('\t\t<echo message="Tagging: ${repo.name}/${image.name} with :latest" />\n')
    lines.append('\t\t<exec executable="docker" failonerror="true">\n')
    lines.append('\t\t\t<arg value="tag" />\n')
    lines.append('\t\t\t<arg value="${repo.name}/${image.name}" />\n')
    lines.append('\t\t\t<arg value="${docker.registry}/${image.name}:latest" />\n')
    lines.append('\t\t</exec>\n')
    lines.append('\t\t<exec executable="docker" failonerror="true">\n')
    lines.append('\t\t\t<arg value="push" />\n')
    lines.append('\t\t\t<arg value="${docker.registry}/${image.name}:latest" />\n')
    lines.append('\t\t</exec>\n')
    lines.append('\t</target>\n')
    lines.append('\n')

    lines.append(ANT_DIVIDER_LINE)

    # add the pull base image helper function
    lines.append('\t<!-- Helper: pull-base-image -->\n')
    lines.append('\t<target name="pull-base-image">\n')
    lines.append('\t\t<fail message="Missing: image.name" unless="image.name" />\n')
    lines.append('\t\t<exec executable="docker" failonerror="true">\n')
    lines.append('\t\t\t<arg value="pull" />\n')
    lines.append('\t\t\t<arg value="${image.name}" />\n')
    lines.append('\t\t</exec>\n')
    lines.append('\t</target>\n')
    lines.append('\n')

    lines.append(ANT_DIVIDER_LINE)

    # add the build tag helper function
    lines.append('\t<!-- Helper: create-build-tag -->\n')
    lines.append('\t<target name="create-build-tag" depends="finish-config">\n')
    lines.append('\t\t<property name="docker.build.tag" value="${app.version}-build-${build.number}" />\n')
    lines.append('\t\t<echo message="Docker build tag: ${docker.build.tag}" />\n')
    lines.append('\t</target>\n')
    lines.append('\n')

    lines.append(ANT_DIVIDER_LINE)

    # finish up
    lines.append('</project>\n')

    if READ_ONLY:
        print('\t' + '\t'.join(lines))
        print('\n')
    else:
        output_file = open(output_path, 'w', newline="\n")
        output_file.writelines(lines)
        output_file.close()

    return


########################################################################################################################


def get_docker_base_image_name(folder_name):
    dockerfile_path = Path(os.path.dirname(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))) / 'docker' / 'images' / folder_name / 'Dockerfile'

    matcher = re.compile('^FROM ([a-zA-Z0-9.:\-/]+)$')

    with open(dockerfile_path) as dockerfile:
        for line in dockerfile:
            result = matcher.match(line)
            if result:
                return result.group(1)

    raise Exception('Unable to find base image!')


########################################################################################################################

if __name__ == "__main__":
    main()

########################################################################################################################
