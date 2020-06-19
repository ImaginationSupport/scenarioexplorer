#!/usr/bin/env python3

import os

from pathlib import Path
from xml.dom import minidom


########################################################################################################################
# to use, first install the "bash-completion" package, then add this line to your bash profile:
#
#   source <path to git repo>/support/helper-scripts/ant-build-bash-completion.sh
#
# then log out and back in
########################################################################################################################

# for additional reference, see: https://iridakos.com/tutorials/2018/03/01/bash-programmable-completion-tutorial.html

def main():
    support_dir = Path(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))

    # parse all of the ant build scripts
    all_targets = []
    for filename in os.listdir(support_dir):
        if filename == 'build.xml' or filename.endswith('.ant'):
            all_targets.extend(parse_ant_build_script(str(support_dir / filename)))

    # generate the output lines
    lines = [
        '#/usr/bin/env bash\n',
        'complete -W "%s" run-dockerized-ant-build.sh\n' % (' '.join(all_targets))
    ]

    output_file = open(support_dir / 'helper-scripts' / 'ant-build-bash-completion.sh', 'w', newline="\n")
    output_file.writelines(lines)
    output_file.close()

    return


########################################################################################################################

def parse_ant_build_script(path):
    print('Processing: %s' % path)

    ant_build_file = minidom.parse(path)

    targets = []
    for target_element in ant_build_file.getElementsByTagName('target'):
        name = target_element.attributes['name'].value
        description = target_element.attributes['description'].value if 'description' in target_element.attributes else None
        print('\t%-50s | %s' % (name, description))

        targets.append(name)

    print()

    return targets


########################################################################################################################

if __name__ == "__main__":
    main()

########################################################################################################################
