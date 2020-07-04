Releasing
========

`X.Y.Z` used as a stand-in for the new version.

1. Checkout `main`: `git checkout main`
1. Ensure the local copy is up to date: `git pull --ff-only`
1. Create a new branch `git checkout -b bump_to_X.Y.Z`
1. Change the `version` in `build.gradle.kts` to the desired version.
1. Update the `CHANGELOG.md` for the impending release, including the date (`YYYY-MM-DD`).
1. Update the `README.md` with the new version.
1. Stage the edited files: `git add build.gradle.kts CHANGELOG.md README.md`
1. Commit the edited files: `git commit -m "Bump version to X.Y.Z."`
1. Push the changes: `git push --set-upstream origin bump_to_X.Y.Z`
1. Create a new pull request & get it merged
1. Create a new Github release:
    * tag name: vX.Y.Z
    * release name: vX.Y.Z Release
1. Verify the pipeline published the package to [Bintray][bintray] successfully.

[bintray]: https://bintray.com/plannigan/com.hypercubetools/ 
