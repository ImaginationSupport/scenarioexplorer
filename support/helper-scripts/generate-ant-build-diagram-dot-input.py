#!/usr/bin/env python3

import os
import sys

from pathlib import Path
from xml.dom import minidom

########################################################################################################################
#
# see: https://graphviz.gitlab.io/_pages/pdf/dotguide.pdf
#
########################################################################################################################


MAX_TARGET_LINE_LENGTH = 10

ENTRY_POINT_BACKGROUND_COLOR = '#FFBB00'
ENTRY_POINT_TEXT_COLOR = '#000000'

NORMAL_DEVELOPER_TASK_BACKGROUND_COLOR = '#4A0000'
NORMAL_DEVELOPER_TASK_TEXT_COLOR = '#FFFFFF'

NORMAL_DEVELOPER_TASKS = [
    'clean',
    'dev-rebuild-web-ui-ara',
    'dev-rebuild-web-ui-ara-auth',
    'dev-rebuild-web-ui-ncsu',

    'build-docker-image-web-ui-ara',
    'build-docker-image-web-ui-ncsu',

    'push-docker-image-web-ui-ara-dev',
    'push-docker-image-web-ui-ncsu-dev',
]


########################################################################################################################


def main():
    support_dir = Path(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))

    output_lines = [
        'digraph G {',
        '\trankdir=LR;'  # make the graph draw vertically which is better for typical browser scrolling
    ]

    # parse all of the ant build scripts
    for filename in os.listdir(support_dir):
        if filename == 'build.xml' or filename.endswith('.ant'):
            # if filename == 'build.xml' or filename == 'build-dev.ant':
            output_lines.extend(parse_ant_build_script(str(support_dir / filename)))

    # add the normal developer tasks entries
    normal_developer_task_node_name = 'normaldevelopertasks'
    node_styles = [
        'label="%s"' % line_split_ant_target_name('Normal-Developer-Tasks'),
        'shape=box',
        'style=filled',
        'color="%s"' % NORMAL_DEVELOPER_TASK_BACKGROUND_COLOR,
        'fontcolor="%s"' % NORMAL_DEVELOPER_TASK_TEXT_COLOR
    ]
    output_lines.append('\t%s [%s];' % (normal_developer_task_node_name, ','.join(node_styles)))
    for target_name in NORMAL_DEVELOPER_TASKS:
        line_styles = ['color=blue']
        output_lines.append('\t%s -> %s [%s];' % (normal_developer_task_node_name, clean_ant_target_name(target_name), ','.join(line_styles)))

    output_lines.append('}')

    output_file = open('ant-build-diagram.gv', 'w', newline="\n")
    output_file.write('\n'.join(output_lines))
    output_file.close()

    return


########################################################################################################################


def clean_ant_target_name(raw_target_name):
    return raw_target_name.replace('-', '___')


########################################################################################################################


def line_split_ant_target_name(raw_target_name):
    if len(raw_target_name) <= MAX_TARGET_LINE_LENGTH:
        return raw_target_name

    combined = []

    current_line = ''
    for part in raw_target_name.split('-'):
        current_line += part + '-'

        if len(current_line) > MAX_TARGET_LINE_LENGTH:
            combined.append(current_line)
            current_line = ''

    if len(current_line) > 0:
        combined.append(current_line)

    return '\\n'.join(combined).rstrip('-')


########################################################################################################################

def parse_ant_build_script(path):
    if verbose_output:
        print('Processing: %s' % path)
        print()

    ant_build_file = minidom.parse(path)

    script_output_lines = []

    for target_element in ant_build_file.getElementsByTagName('target'):
        name = target_element.attributes['name'].value
        description = target_element.attributes['description'].value if 'description' in target_element.attributes else None
        depends = target_element.attributes['depends'].value.replace(' ', '') if 'depends' in target_element.attributes else None

        if verbose_output:
            print('\t%-60s | %s' % (name, depends))

        target_name_cleaned = clean_ant_target_name(name)

        node_styles = ['label="%s"' % line_split_ant_target_name(name)]

        if description:
            node_styles.append('shape=box')
            node_styles.append('style=filled')
            node_styles.append('color="%s"' % ENTRY_POINT_BACKGROUND_COLOR)
            node_styles.append('fontcolor="%s"' % ENTRY_POINT_TEXT_COLOR)

        script_output_lines.append('\t%s [%s];' % (target_name_cleaned, ','.join(node_styles)))

        if depends:
            for dependency in depends.split(','):
                script_output_lines.append('\t%s -> %s' % (clean_ant_target_name(dependency), target_name_cleaned))

        ant_call_target_counts = {}
        for child_node in target_element.getElementsByTagName('antcall'):
            ant_call_target = child_node.attributes['target'].value
            if ant_call_target in ant_call_target_counts:
                ant_call_target_counts[ant_call_target] += 1
            else:
                ant_call_target_counts[ant_call_target] = 1
        for ant_call_target, count in ant_call_target_counts.items():
            line_styles = ['color=red']

            if count > 1:
                line_styles.append('label="%dx"' % count)

            script_output_lines.append('\t%s -> %s [%s]' % (clean_ant_target_name(ant_call_target), target_name_cleaned, ','.join(line_styles)))

    if verbose_output:
        print()

    return script_output_lines


########################################################################################################################

verbose_output = False

for arg in sys.argv:
    if arg == '-v':
        verbose_output = True

if __name__ == "__main__":
    main()

########################################################################################################################
